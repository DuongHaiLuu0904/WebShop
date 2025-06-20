package com.ptit.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ptit.service.CategoryService;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Component
public class GlobalInterceptor implements HandlerInterceptor {

    @Autowired
    CategoryService categoryService;

    @Override
    public void postHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler,
        @Nullable ModelAndView modelAndView) throws Exception {
        request.setAttribute("cates", categoryService.findAll());
    }

}
