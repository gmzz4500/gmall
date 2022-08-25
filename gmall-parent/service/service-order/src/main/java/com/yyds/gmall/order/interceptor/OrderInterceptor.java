package com.yyds.gmall.order.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @ClassName: OrderInterceptor
 * @Author: yyd
 * @Date: 2022/8/10/010
 * @Description: 订单微服务的拦截器,拦截所有发出去的feign请求
 */
@Component
public class OrderInterceptor implements RequestInterceptor {
    
    /**
     * 在http请求发出去前触发,触发完成以后,http请求发送
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes!=null) {
            //获取请求体
            HttpServletRequest request = servletRequestAttributes.getRequest();
            //将元request的请求头中的参数拿出来,放入到requestTemplate的请求头中去
            Enumeration<String> headerNames = request.getHeaderNames();
            //遍历一个个放入现在的request
            while (headerNames.hasMoreElements()){
                //获取参数的名字
                String name = headerNames.nextElement();
                //获取参数的值
                String value = request.getHeader(name);
                //存储
                requestTemplate.header(name, value);
            }
        }
    }
}
