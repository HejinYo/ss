package cn.hejinyo.ss.security.core.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/19 20:35
 */
@Slf4j
public class BaseFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        log.error("请求的URI===>{}", httpServletRequest.getRequestURI());
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
