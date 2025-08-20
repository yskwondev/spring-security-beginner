package com.practice.securitybeginner.security;

import com.practice.securitybeginner.interceptor.exception.AuthenticateException;
import com.practice.securitybeginner.security.domain.CustomUserDetails;
import com.practice.securitybeginner.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.practice.securitybeginner.enums.ErrorCode.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final UserDetailsService userDetailsService;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication) {
    JwtAuthenticationToken authToken = (JwtAuthenticationToken) authentication;
    return authenticateLogin(authToken);
  }

  private Authentication authenticateLogin(JwtAuthenticationToken authToken) throws AuthenticateException {
    String username = (String) authToken.getPrincipal();
    String password = (String) authToken.getCredentials();

    CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

    if (!passwordEncoder.matches(password, userDetails.getPassword())) { throw new AuthenticateException(USER_INFO_MISMATCH); }
    if (!userDetails.isEnabled()) { throw new AuthenticateException(DISABLED_USER); }
    if (!userDetails.isAccountNonLocked()) { throw new AuthenticateException(LOCKED_USER); }

    userDetails.getUser().updateLastLoginDateTime();
    userService.updateLastLoginDate(userDetails.getUser());

    return JwtAuthenticationToken.authenticated(userDetails.getUser(), userDetails.getAuthorities());

  }

  // 이 provider가 특정 인증객체를 처리할 수 있는지 여부를 리턴함
  // 구현체가 인증객체를 무엇을 보내냐에 따라 여러개의 인증을 시도할 수 있도록 함.
  @Override
  public boolean supports(Class<?> authentication) {
    return JwtAuthenticationToken.class.isAssignableFrom(authentication);
  }

}
