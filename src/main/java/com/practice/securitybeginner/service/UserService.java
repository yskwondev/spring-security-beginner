package com.practice.securitybeginner.service;

import com.practice.securitybeginner.domain.ApplicationUser;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface UserService {

  Optional<ApplicationUser> selectUserById(String userId);
}
