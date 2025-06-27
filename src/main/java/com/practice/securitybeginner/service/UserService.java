package com.practice.securitybeginner.service;

import com.practice.securitybeginner.domain.ApplicationUser;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface UserService {

  Optional<ApplicationUser> findUserById(String userId);

  void createUser(ApplicationUser user);

  void createUserRoles(ApplicationUser user);

  void updateLastLoginDate(ApplicationUser user);

}
