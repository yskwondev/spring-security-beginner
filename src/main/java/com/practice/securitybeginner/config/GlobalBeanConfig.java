package com.practice.securitybeginner.config;

import com.practice.securitybeginner.security.JwtAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class GlobalBeanConfig {

  @Bean
  public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
    // add additional security auth provider
    JwtAuthenticationProvider provider = new JwtAuthenticationProvider(userDetailsService, passwordEncoder());
    return new ProviderManager(provider);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
