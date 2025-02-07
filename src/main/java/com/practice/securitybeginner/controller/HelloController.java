package com.practice.securitybeginner.controller;

import com.practice.securitybeginner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class HelloController {

  private final UserService userService;

  @GetMapping("/hello")
  public String hello(String name) {
//    ApplicationUser user = userService.selectUserById("test@naver.com");
    return "Hello " + name;
  }

}
