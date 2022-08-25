package com.yyds.gmall.gateway.filter;

import com.yyds.gmall.gateway.util.IpUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @ClassName: GmallFilter
 * @Author: yyd
 * @Date: 2022/8/8/008
 * @Description: 自定义全局过滤器
 */
@Component
public class GmallFilter implements GlobalFilter, Ordered {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 自定义过滤器逻辑
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求体
        ServerHttpRequest request = exchange.getRequest();
        //获取响应体
        ServerHttpResponse response = exchange.getResponse();
        //从用户请求的url中获取token参数
        String token = request.getQueryParams().getFirst("token");
        //若url中没有,则从head中取token
        if (StringUtils.isEmpty(token)){
            token = request.getHeaders().getFirst("token");
            if (StringUtils.isEmpty(token)){
                //从cookie中获取token
                MultiValueMap<String, HttpCookie> cookies = request.getCookies();
                if (cookies != null && cookies.size() > 0){
                    HttpCookie cookiesFirst = cookies.getFirst("token");
                    if (cookiesFirst != null){
                        token = cookiesFirst.getValue();
                    }
                }
            }
        }
        //若全部都没有,拒绝用户请求
        if (StringUtils.isEmpty(token)){
            response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            return response.setComplete();
        }
        //获取当前的ip地址
        String gatwayIpAddress = IpUtil.getGatwayIpAddress(request);
        //使用ip地址从redis中获取令牌
        String redisToken = stringRedisTemplate.opsForValue().get(gatwayIpAddress);
        if (StringUtils.isEmpty(redisToken)){
            //用户没有登陆过
            response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            return response.setComplete();
        }
        //判断redis中和用户给的是否一致
        if (!redisToken.equals(token)) {
            response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            return response.setComplete();
        }
    
        //将token以固定的格式存储到request的请求头中去,key的名字固定 value的格式固定
        request.mutate().header("Authorization","bearer " + token);
        //放行
        return chain.filter(exchange);
    }
    
    /**
     * 过滤器的执行顺序:数值越小,优先级越高
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
