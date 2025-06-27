package com.ptit.interceptor;

import com.ptit.entity.Customers;
import com.ptit.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private CustomerService customerService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        HttpSession session = request.getSession();
        
        // Only set authentication data if user is authenticated and session doesn't have it
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()) 
            && session.getAttribute("authentication") == null) {
            
            try {
                String username = auth.getName();
                Customers user = customerService.findByUsername(username);
                
                // Nếu không tìm thấy user theo username, thử tìm theo email
                if (user == null) {
                    user = customerService.findByEmail(username);
                }
                
                // Nếu vẫn không tìm thấy user, bỏ qua việc tạo authentication
                if (user != null) {
                    Map<String, Object> authMap = new HashMap<>();
                    
                    // Tạo user object đơn giản cho JSON serialization
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", user.getId()); // Add customer ID
                    userInfo.put("username", username);
                    userInfo.put("fullname", user.getFullname());
                    userInfo.put("email", user.getEmail());
                    userInfo.put("photo", user.getPhoto());

                    authMap.put("user", userInfo);
                    byte[] token = (username + ":" + user.getPassword()).getBytes();
                    authMap.put("token", "Basic " + Base64.getEncoder().encodeToString(token));
                    session.setAttribute("authentication", authMap);
                }
                
            } catch (Exception e) {
                // Log error but don't fail the request
                e.printStackTrace();
            }
        }
        
        return true;
    }
}
