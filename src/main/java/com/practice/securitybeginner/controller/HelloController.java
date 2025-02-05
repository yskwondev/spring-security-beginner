package com.practice.securitybeginner.controller;

import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.security.JwtTokenUtil;
import com.practice.securitybeginner.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class HelloController {

  private final UserService userService;

  @GetMapping("/hello")
  public String hello(String name) {

//    ApplicationUser user = userService.selectUserById("test@naver.com");

    return "Hello " + name;
  }

  @GetMapping("/hello2")
    public String hello2(String name) {

  //    ApplicationUser user = userService.selectUserById("test@naver.com");

      return "Hello2 " + name;
    }

}
