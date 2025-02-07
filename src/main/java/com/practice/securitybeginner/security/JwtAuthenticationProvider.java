package com.practice.securitybeginner.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.security.domain.CustomUserDetails;
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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final UserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final ObjectMapper objectMapper;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    JwtAuthenticationToken authToken = (JwtAuthenticationToken) authentication;
    return authenticateLogin(authToken);
  }

  private Authentication authenticateLogin(JwtAuthenticationToken authToken) {
    String username = (String) authToken.getPrincipal();
    String password = (String) authToken.getCredentials();

    CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
      throw new BadCredentialsException("Invalid password");
    }

    if (!userDetails.isEnabled()) { throw new BadCredentialsException("User is disabled"); }
    if (!userDetails.isAccountNonLocked()) { throw new BadCredentialsException("User is locked"); }

    Map<String, Object> convertedUserDetails = objectMapper.convertValue(userDetails.getUser(), new TypeReference<>() {});
    return new JwtAuthenticationToken(username, convertedUserDetails, userDetails.getAuthorities());
  }

  // 이 provider가 특정 인증객체를 처리할 수 있는지 여부를 리턴함
  // 구현체가 인증객체를 무엇을 보내냐에 따라 여러개의 인증을 시도할 수 있도록 함.
  @Override
  public boolean supports(Class<?> authentication) {
    return JwtAuthenticationToken.class.isAssignableFrom(authentication);
  }

}
