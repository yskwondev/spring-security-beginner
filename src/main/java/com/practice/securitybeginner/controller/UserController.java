package com.practice.securitybeginner.controller;

import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.enums.ErrorCode;
import com.practice.securitybeginner.interceptor.exception.AuthenticateException;
import com.practice.securitybeginner.interceptor.exception.NotFoundException;
import com.practice.securitybeginner.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

}
