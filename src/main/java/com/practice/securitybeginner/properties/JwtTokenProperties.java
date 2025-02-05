package com.practice.securitybeginner.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("token")
@RequiredArgsConstructor @Getter
@ToString
public class JwtTokenProperties {

  private final String secretKey;
  private final Duration expiredTime;

}


