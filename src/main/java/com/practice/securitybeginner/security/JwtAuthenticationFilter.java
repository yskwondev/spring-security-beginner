package com.practice.securitybeginner.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenUtil jwtTokenUtil;
  private final UserDetailsService userDetailsService;
  private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) throws ServletException, IOException {

    final String authorizationHeader = request.getHeader(AUTHORIZATION);
    final String token;
    final String userId;

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      token = authorizationHeader.substring(7);
      userId = jwtTokenUtil.extractClaim(token, Claims::getSubject);

      logger.info("[ token :: {} ]", token);
      logger.info("[ userId :: {} ]", userId);

      if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        // 아직 인증 받지 못한 사용자
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId); // 인증 1
        if (jwtTokenUtil.isTokenValid(token, userDetails)) {
          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userDetails, userDetails.getPassword(), userDetails.getAuthorities()
          );
          // 토큰에 싣고싶은 additional 데이터 추가
          authenticationToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
          );
//          Authentication authentication = authenticationManager.authenticate(authenticationToken); // 인증 2 :: 두번하는 이유가 있나?
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
      }
    }
    filterChain.doFilter(request, response);
  }

}
