package com.ptit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ptit.interceptor.GlobalInterceptor;
import com.ptit.interceptor.AuthenticationInterceptor;

import org.springframework.lang.NonNull;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    GlobalInterceptor globalInterceptor;
    
    @Autowired
    AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**");
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(globalInterceptor).addPathPatterns("/**").excludePathPatterns("/rest/**", "/admin/**",
                "/assets/**", "/assetss/**");
        registry.addInterceptor(authenticationInterceptor).addPathPatterns("/**");
    }

}
