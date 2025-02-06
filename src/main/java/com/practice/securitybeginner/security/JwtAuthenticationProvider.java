package com.practice.securitybeginner.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final JwtTokenUtil jwtTokenUtil;
  private final UserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    JwtAuthenticationToken authToken = (JwtAuthenticationToken) authentication;

    // username/password 로그인
    if (authToken.getPrincipal() != null) {
        return authenticateLogin(authToken);
    }

    // api 호출(JWT 토큰 검증)
    return authenticateToken(authToken);
  }

  private Authentication authenticateLogin(JwtAuthenticationToken authToken) {
      String username = (String) authToken.getPrincipal();
      String password = (String) authToken.getCredentials();

      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      if (!passwordEncoder.matches(password, userDetails.getPassword())) {
          throw new BadCredentialsException("Invalid password");
      }

      return new JwtAuthenticationToken(username, userDetails.getAuthorities());
  }

  private Authentication authenticateToken(JwtAuthenticationToken authToken) {
      String token = (String) authToken.getCredentials();

      if (!jwtTokenUtil.validateToken(token)) {
          throw new BadCredentialsException("Invalid JWT token");
      }

      String username = jwtTokenUtil.extractClaim(token, Claims::getSubject);
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      return new JwtAuthenticationToken(username, userDetails.getAuthorities());
  }

  // 이 provider가 특정 인증객체를 처리할 수 있는지 여부를 리턴함
  // 구현체가 인증객체를 무엇을 보내냐에 따라 여러개의 인증을 시도할 수 있도록 함.
  @Override
  public boolean supports(Class<?> authentication) {
    return JwtAuthenticationToken.class.isAssignableFrom(authentication);
  }

}
