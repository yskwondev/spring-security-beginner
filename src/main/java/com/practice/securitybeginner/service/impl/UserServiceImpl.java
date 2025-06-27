package com.practice.securitybeginner.service.impl;

import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.mapper.UserMapper;
import com.practice.securitybeginner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserMapper userMapper;

  @Override
  public Optional<ApplicationUser> findUserById(String userId) {
    return userMapper.findUserById(userId);
  }

  @Override
  public void createUser(ApplicationUser user) {
    userMapper.createUser(user);
  }

  @Override
  public void createUserRoles(ApplicationUser user) {
    userMapper.createUserRoles(user);
  }

  @Override
  public void updateLastLoginDate(ApplicationUser user) {
    userMapper.updateLastLoginDate(user);
  }
}
