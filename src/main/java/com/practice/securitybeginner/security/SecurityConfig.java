package com.practice.securitybeginner.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

//  private final JwtAuthenticationProvider jwtAuthenticationProvider;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  private final String[] PERMITTED_URL = {
    "/login",
    "/hello",
  };

  @Bean
  public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {

    http
      .headers(
        headersConfigurer -> headersConfigurer.frameOptions(
          HeadersConfigurer.FrameOptionsConfig::sameOrigin
        )
      )
      .cors(withDefaults())
      .csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(
        req -> req.requestMatchers(
            PERMITTED_URL
        ).permitAll()
          .anyRequest()
            .authenticated()
      )
      .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
//      .authenticationProvider(jwtAuthenticationProvider)
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();

  }

}
