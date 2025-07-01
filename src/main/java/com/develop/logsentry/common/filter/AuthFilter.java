package com.develop.logsentry.common.filter;

import com.develop.logsentry.common.jwt.JwtUtil;
import com.develop.logsentry.common.security.UserDetailsImpl;
import com.develop.logsentry.domain.user.entity.User;
import com.develop.logsentry.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Order(2)
@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private static final Set<String> EXACT_PATHS = Set.of("/v1/user/signup", "/v1/user/login");
    private static final String[] SWAGGER_PATH_PREFIXES = {
            "/swagger", "/v3/api-docs", "/swagger-ui", "/swagger-resources", "/webjars"
    };

    private boolean isSwaggerRequest(String uri) {
        for (String prefix : SWAGGER_PATH_PREFIXES) {
            if (uri.startsWith(prefix)) return true;
        }
        return false;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (EXACT_PATHS.contains(uri) || isSwaggerRequest(uri)) {
            chain.doFilter(request, response);
            return;
        }

        String token = jwtUtil.resolveToken(request);
        if (token == null) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token not provided");
            return;
        }

        try {
            if (jwtUtil.validateToken(token)) {
                Claims claims = jwtUtil.getUserInfoFromToken(token);
                User user = userRepository.findByEmailAndIsActiveTrueOrThrow(claims.getSubject());
                request.setAttribute("user", user);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetailsImpl userDetails = new UserDetailsImpl(user);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
        } catch (Exception e) {
            log.error("Token validation error for URI: {}", uri, e);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            return;
        }

        chain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}