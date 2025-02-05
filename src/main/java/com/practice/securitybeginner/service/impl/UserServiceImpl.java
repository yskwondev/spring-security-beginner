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
  public Optional<ApplicationUser> selectUserById(String userId) {
    return userMapper.selectUserById(userId);
  }
}
