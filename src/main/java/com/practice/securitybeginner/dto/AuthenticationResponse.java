package com.practice.securitybeginner.dto;

import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Builder
@Getter
@Setter
@ToString
public class AuthenticationResponse {
  private String accessToken;
  private ApplicationUser user;
}
