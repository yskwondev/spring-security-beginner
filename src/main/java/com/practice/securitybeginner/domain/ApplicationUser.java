package com.practice.securitybeginner.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.practice.securitybeginner.enums.Role;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUser implements Serializable {

  @Serial
  private static final long serialVersionUID = 1234567890L;

  private String userId;
  private String userName;
  @JsonIgnore
  private String password;
  @JsonIgnore
  private boolean accountLocked;
  @JsonIgnore
  private boolean accountEnabled;
  private LocalDateTime lastLoginDateTime;
  private LocalDate createDate;
  private Set<Role> roles;

}
