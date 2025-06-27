package com.practice.securitybeginner.controller;

import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.enums.ErrorCode;
import com.practice.securitybeginner.interceptor.exception.AuthenticateException;
import com.practice.securitybeginner.interceptor.exception.NotFoundException;
import com.practice.securitybeginner.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

  private final UserService userService;

  @GetMapping("/find-user/{userId}")
  public ApplicationUser findUser(@PathVariable String userId) throws AuthenticateException {
    return userService.findUserById(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
  }

}
