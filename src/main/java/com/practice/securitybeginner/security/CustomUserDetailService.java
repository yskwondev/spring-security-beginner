package com.practice.securitybeginner.security;

import com.practice.securitybeginner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

  private final UserService userService;

  @Override
  public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
    return userService.findUserById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

}
