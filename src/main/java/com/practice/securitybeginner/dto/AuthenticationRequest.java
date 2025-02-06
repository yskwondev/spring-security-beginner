package com.practice.securitybeginner.dto;

import com.practice.securitybeginner.enums.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
public class AuthenticationRequest {
  private String email;
  private String password;
  private String userName;
  private Set<Role> roles;
}
