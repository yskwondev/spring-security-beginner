package com.practice.securitybeginner.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthenticationRequest {
  private String userId;
  private String password;
}
