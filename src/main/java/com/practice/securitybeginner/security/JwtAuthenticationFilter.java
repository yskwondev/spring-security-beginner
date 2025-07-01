package com.practice.securitybeginner.security;

import com.practice.securitybeginner.enums.ErrorCode;
import com.practice.securitybeginner.interceptor.exception.AuthenticateException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;

import static com.practice.securitybeginner.enums.ErrorCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenUtil jwtTokenUtil;

  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) throws ServletException, IOException {

    try {
      final String token = extractAccessToken(request);
      if (!StringUtils.hasText(token)) throw new AuthenticateException(MISSING_ACCESS_TOKEN);
      if (!jwtTokenUtil.validateToken(token)) throw  new AuthenticateException(EXPIRED_ACCESS_TOKEN);
      Authentication authentication = jwtTokenUtil.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (Exception ex) {
      SecurityContextHolder.clearContext();
      request.setAttribute("exception", ex); // filter 예외처리를 위해 예외 객체 넘김
    } finally {
      filterChain.doFilter(request, response);
    }

  }

  private String extractAccessToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
        return bearerToken.substring(7);
    }
    return null;
  }

}
