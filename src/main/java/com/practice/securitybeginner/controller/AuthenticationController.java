package com.practice.securitybeginner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.dto.AuthenticationRequest;
import com.practice.securitybeginner.dto.AuthenticationResponse;
import com.practice.securitybeginner.enums.Role;
import com.practice.securitybeginner.interceptor.exception.AuthenticateException;
import com.practice.securitybeginner.security.JwtAuthenticationToken;
import com.practice.securitybeginner.security.domain.JwtToken;
import com.practice.securitybeginner.security.domain.TokenPair;
import com.practice.securitybeginner.security.service.JwtTokenService;
import com.practice.securitybeginner.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static com.practice.securitybeginner.enums.ErrorCode.DUPLICATE_EMAIL;
import static com.practice.securitybeginner.enums.ErrorCode.MISSING_REFRESH_TOKEN;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;
  private final UserService userService;
  private final JwtTokenService jwtTokenService;
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/refresh")
  public AuthenticationResponse refeshAuthToken(@CookieValue(name = "${token.refresh-token-cookie-key}") String refreshToken, HttpServletResponse response) throws AuthenticateException {
    if (!StringUtils.hasText(refreshToken)) {
      throw new AuthenticateException(MISSING_REFRESH_TOKEN);
    }
    JwtToken newAccessToken = jwtTokenService.reissueAccessToken(refreshToken);
    return AuthenticationResponse.builder().accessToken(newAccessToken.getValue()).user(ApplicationUser.from(newAccessToken)).build();
  }

  @PostMapping("/sign-up")
  public AuthenticationResponse signUp(@RequestBody AuthenticationRequest authenticationRequest) throws AuthenticateException {
    if (userService.findUserById(authenticationRequest.getUserId()).isPresent()) {
      throw new AuthenticateException(DUPLICATE_EMAIL);
    }

    ApplicationUser user = ApplicationUser.builder()
      .userId(authenticationRequest.getUserId())
      .userName(authenticationRequest.getUserName())
      .password(passwordEncoder.encode(authenticationRequest.getPassword()))
      .roles(Set.of(Role.USER, Role.ADMIN))
      .build();

    userService.createUser(user);
    userService.createUserRoles(user);

    return AuthenticationResponse.builder().accessToken(null).user(user).build();
  }

  @PostMapping("/sign-in")
  public AuthenticationResponse signIn(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) throws AuthenticateException {
    Authentication userAuthentication = authenticationManager.authenticate(
      JwtAuthenticationToken.unauthenticated(authenticationRequest.getUserId(), authenticationRequest.getPassword()) // 인증시도를 위해 임시토큰 생성 후 전달
    );

    ApplicationUser loginUser = (ApplicationUser) userAuthentication.getDetails();
    // token pair setting
    TokenPair tokenPair = jwtTokenService.issueTokenPair(loginUser);
    // set cookie for refresh token
    response.addHeader(HttpHeaders.SET_COOKIE, jwtTokenService.issueRefreshTokenCookie(tokenPair.getRefreshToken().getValue()));

    return AuthenticationResponse.builder().accessToken(tokenPair.getAccessToken().getValue()).user(loginUser).build();
  }

}
