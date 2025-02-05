package com.practice.securitybeginner.controller;

import com.practice.securitybeginner.dto.AuthenticationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

  @PostMapping("/sign-in")
  public ResponseEntity<AuthenticationRequest> signIn(@RequestBody AuthenticationRequest authenticationRequest) {
    logger.info("Authentication request: {}", authenticationRequest);

    return null;
  }

}
