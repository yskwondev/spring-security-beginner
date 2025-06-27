package com.practice.securitybeginner.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  private final String[] ACCESSIBLE_URL = {
    "/css/**", "/images/**", "/js/**", "/favicon.*", "/*/icon-*",
    "/hello",
    "/api/auth/**"
  };

  @Bean
  public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {

    http
      .headers(
        headersConfigurer -> headersConfigurer.frameOptions(
          HeadersConfigurer.FrameOptionsConfig::sameOrigin
        )
      )
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))
      .csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(
        req -> req.requestMatchers(
          ACCESSIBLE_URL
        ).permitAll()
          .anyRequest()
            .authenticated()
      )
      .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
      .exceptionHandling(handling -> handling
          .authenticationEntryPoint(customAuthenticationEntryPoint)
      )
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();

  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
      return config.getAuthenticationManager();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of("*")); // 모든 도메인 허용
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE")); // 모든 메소드 허용
    configuration.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
    configuration.setAllowCredentials(true); // 쿠키나 인증헤더 포함 여부? todo 추가 확인 필요

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 적용 todo 필요한 url만 오픈해야할듯 (localhost, 기타 외부접근 허용 url)

    return source;

  }

}
