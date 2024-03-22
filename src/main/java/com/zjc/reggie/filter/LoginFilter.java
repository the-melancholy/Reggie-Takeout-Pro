package com.zjc.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.zjc.reggie.common.BaseContext;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.dto.LoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录过滤器，检测用户是否登录

 */
@WebFilter(filterName = "LoginFilter",urlPatterns = "/*")
@Slf4j
public class LoginFilter implements Filter {


    public static  String[] urls = new String[]{
            "/employee/login",
            "/employee/logout",
            //静态资源可以放行
            "/backend/**",
            "/front/**",
            "/user/login",
            "/user/sendMsg"
    };

    //AntPathMatcher可以将路径按通配符方式匹配
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     *对请求进行拦截，而不是对网址进行拦截
     */
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取本次请求URI
        String requestURI = request.getRequestURI();

        //路径匹配,判断是否需要进行登录拦截
        Boolean check = check(urls, requestURI);
        //无需登录的页面
        if(check){
            filterChain.doFilter(request,response);
            return;
        }

        //后端用户-如果已登录，则直接放行
        LoginDTO employee = (LoginDTO) request.getSession().getAttribute("employee");
        if(employee!=null){
            BaseContext.save(employee);
            filterChain.doFilter(request,response);
            return;
        }
        //前端用户-如果已登录，则直接放行
        LoginDTO client = (LoginDTO) request.getSession().getAttribute("user");
        if(client!=null){
            BaseContext.save(client);
            filterChain.doFilter(request,response);
            return;
        }

        //如果未登录，通过输出流方式向客户端页面响应数据，交给前端处理
        response.getWriter().write(JSON.toJSONString(Result.error("NOT LOGIN")));

    }

    public Boolean check(String[] urls,String requestUrl){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestUrl);
            if (match){
                return true;
            }
        }
        return false;


    }
}
