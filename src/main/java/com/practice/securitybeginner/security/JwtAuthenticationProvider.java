package com.practice.securitybeginner.security;

import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final UserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationProvider.class);

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) authentication;
    String userId = authToken.getName();
    String password = authToken.getCredentials().toString();

    logger.debug("[ USER ID ] :: {}", userId);
    logger.debug("[ PASSWORD ] :: {}", password);
    logger.debug("[ ENCODE PASSWORD ] :: {}", passwordEncoder.encode(password));

    UserDetails user = userDetailsService.loadUserByUsername(userId);

    if (passwordEncoder.matches(password, user.getPassword())) {
      return new UsernamePasswordAuthenticationToken(userId, password, user.getAuthorities());
    } else {
      throw new BadCredentialsException("Bad Credentials");
    }

  }

  // 이 provider가 특정 인증객체를 처리할 수 있는지 여부를 리턴함
  // 구현체가 인증객체를 무엇을 보내냐에 따라 여러개의 인증을 시도할 수 있도록 함.
  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

}
