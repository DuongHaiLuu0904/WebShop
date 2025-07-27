package com.ptit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.ptit.entity.Customers;
import com.ptit.service.CustomerService;

import java.util.Collection;
import java.util.NoSuchElementException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomerService customerService;

    public SecurityConfig(CustomerService customerService) {
        this.customerService = customerService;
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
                Customers user = customerService.findByUsername(username);
                if (user == null) {
                    throw new UsernameNotFoundException(username + " not found!");
                }
                
                // Nếu password đã được encode (bắt đầu bằng $2a$ hoặc $2b$), không encode lại
                String password = user.getPassword();
                if (!password.startsWith("$2a$") && !password.startsWith("$2b$")) {
                    password = passwordEncoder.encode(password);
                }
                
                Collection<GrantedAuthority> authorities = user.getAuthorities();

                return User.withUsername(username).password(password).authorities(authorities).build();
                
            } catch (NoSuchElementException e) {
                throw new UsernameNotFoundException(username + " not found!");
            } catch (Exception e) {
                throw new UsernameNotFoundException(username + " authentication failed: " + e.getMessage());
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

    /* Phân quyền */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/admin/**").authenticated()  // Tạm thời cho phép mọi user đã login
                        .requestMatchers("/rest/authorities", "/rest/customers").authenticated()  // Chỉ cần login
                        .anyRequest().permitAll())
                
                .formLogin(form -> form
                        .loginPage("/auth/login/form")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/auth/login/success", false)
                        .failureUrl("/auth/login/error"))
                
                .rememberMe(remember -> remember
                        .key("mySecretKey")
                        .rememberMeParameter("remember-me") // Tên parameter từ form
                        .tokenValiditySeconds(86400 * 7) // 7 ngày 
                        .userDetailsService(userDetailsService(getPasswordEncoder())))
                
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/auth/login/form")
                        .defaultSuccessUrl("/oauth2/login/success", false)
                        .failureUrl("/auth/login/error")
                        .authorizationEndpoint(authorization -> authorization
                        .baseUri("/oauth2/authorization")))
                
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login/form"))
                
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/auth/unauthoried"));

        return http.build();
    }

    // @Bean
    // public WebSecurityCustomizer webSecurityCustomizer() {
    // return (web) -> web.ignoring().requestMatchers("/assets/**");
    // }
}
