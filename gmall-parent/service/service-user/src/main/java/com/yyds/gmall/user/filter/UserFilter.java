package com.yyds.gmall.user.filter;

import com.yyds.gmall.user.util.TokenUtil;
import com.yyds.gmall.user.util.UserThreadLocalUtil;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @ClassName: UserFilter
 * @Author: yyd
 * @Date: 2022/8/9/009
 * @Description: 自定义user微服务的过滤器
 */
@WebFilter(filterName = "userFilter", urlPatterns = "/*")
@Order(1)
public class UserFilter extends GenericFilterBean {
    /**
     * 自定义过滤器
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        //request类型转换
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //获取令牌
        String token = request.getHeader("Authorization").replace("bearer ", "");
        //获取载荷
        Map<String, String> map = TokenUtil.dcodeToken(token);
        if (!map.isEmpty()) {
            //获取用户名
            String username = map.get("username");
            //存储用户名
            UserThreadLocalUtil.set(username);
        }
        //放行
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
