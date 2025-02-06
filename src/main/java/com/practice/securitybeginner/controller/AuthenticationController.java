package com.practice.securitybeginner.controller;

import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.dto.AuthenticationRequest;
import com.practice.securitybeginner.enums.Role;
import com.practice.securitybeginner.security.JwtAuthenticationToken;
import com.practice.securitybeginner.security.JwtTokenUtil;
import com.practice.securitybeginner.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenUtil jwtTokenUtil;

  @PostMapping("/sign-up")
  public ResponseEntity<?> signUp(@RequestBody AuthenticationRequest authenticationRequest) {
    if (userService.findUserById(authenticationRequest.getEmail()).isPresent()) {
      return ResponseEntity.badRequest().body("User ID already exist");
    }

    ApplicationUser user = ApplicationUser.builder()
      .email(authenticationRequest.getEmail())
      .userName(authenticationRequest.getUserName())
      .password(passwordEncoder.encode(authenticationRequest.getPassword()))
      .roles(Set.of(Role.USER))
      .build();

    userService.createUser(user);
    userService.createUserRoles(user);

    return ResponseEntity.ok("Create User Success");
  }

  @PostMapping("/sign-in")
  public ResponseEntity<String> signIn(@RequestBody AuthenticationRequest authenticationRequest) {
    log.info("Authentication request: {}", authenticationRequest);
    Authentication userAuthentication = authenticationManager.authenticate(
      new JwtAuthenticationToken(
        authenticationRequest.getEmail(),
        authenticationRequest.getPassword()
      )
    );
    String token = jwtTokenUtil.generateToken(userAuthentication);
    return ResponseEntity.ok(token);
  }

}
