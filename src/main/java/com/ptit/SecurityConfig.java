package com.ptit;

import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.ptit.entity.Customers;
import com.ptit.service.CustomerService;

import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomerService customerService;
    private final HttpSession session;

    public SecurityConfig(CustomerService customerService, HttpSession session) {
        this.customerService = customerService;
        this.session = session;
    }

    /* Cơ chế mã hóa mật khẩu */
    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* Quản lý dữ liệu người sử dụng */
    @Bean
    public UserDetailsService userDetailsService(BCryptPasswordEncoder passwordEncoder) {
        return username -> {
            try {
                Customers user = customerService.findById(username);
                String password = passwordEncoder.encode(user.getPassword());

                Collection<GrantedAuthority> authorities = user.getAuthorities();
                Map<String, Object> authentication = new HashMap<>();
                
                // Tạo user object đơn giản cho JSON serialization
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("username", username);
                userInfo.put("fullname", user.getFullname());
                userInfo.put("email", user.getEmail());
                userInfo.put("photo", user.getPhoto());
                
                authentication.put("user", userInfo);
                byte[] token = (username + ":" + user.getPassword()).getBytes();
                authentication.put("token", "Basic " + Base64.getEncoder().encodeToString(token));
                session.setAttribute("authentication", authentication);
                return User.withUsername(username)
                        .password(password)
                        .authorities(authorities)
                        .build();
            } catch (NoSuchElementException e) {
                throw new UsernameNotFoundException(username + " not found!");
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Tắt thuật tấn công giả mạo
        http.csrf(csrf -> csrf.disable());

        // Quyền yêu cầu truy cập
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/order/**", "/auth/change-password").authenticated()
                .requestMatchers("/admin/**").hasAnyAuthority("ROLE_STAF", "ROLE_DIRE")
                .requestMatchers("/rest/authorities").hasAuthority("ROLE_DIRE")
                .anyRequest().permitAll());

        // Đăng nhập
        http.formLogin(form -> form
                .loginPage("/auth/login/form")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/auth/login/success", false)
                .failureUrl("/auth/login/error"));
        // Ghi nhớ
        // http.rememberMe(rememberMe -> rememberMe.tokenValiditySeconds(86400));
        // Điều khiển lỗi truy cập không đúng quyền
        http.exceptionHandling(exceptionHandling -> exceptionHandling.accessDeniedPage("/auth/unauthoried"));
        // Đăng xuất
        http.logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/logout/success"));
        // OAuth2 - Đăng nhâp từ mang xã hội
        // http.oauth2Login(oauth2 -> oauth2
        // .loginPage("/auth/login/form")
        // .defaultSuccessUrl("/oauth2/login/success", true)
        // .failureUrl("/auth/login/error")
        // .authorizationEndpoint(authorization ->
        // authorization.baseUri("/oauth2/authorization"))
        // );
        return http.build();
    }

    /* Cho phép truy xuất REST API từ domain khác */
    // @Bean
    // public WebSecurityCustomizer webSecurityCustomizer() {
    // return web -> web.ignoring().requestMatchers(HttpMethod.OPTIONS, "/**");
    // }
}