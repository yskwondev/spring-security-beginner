package com.practice.securitybeginner.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.dto.AuthenticationRequest;
import com.practice.securitybeginner.dto.AuthenticationResponse;
import com.practice.securitybeginner.enums.Role;
import com.practice.securitybeginner.interceptor.exception.AuthenticateException;
import com.practice.securitybeginner.security.JwtAuthenticationToken;
import com.practice.securitybeginner.security.JwtTokenUtil;
import com.practice.securitybeginner.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static com.practice.securitybeginner.enums.ErrorCode.DUPLICATE_EMAIL;
import static com.practice.securitybeginner.enums.ErrorCode.EXPIRED_REFRESH_TOKEN;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;
  private final UserService userService;
  private final JwtTokenUtil jwtTokenUtil;
  private final PasswordEncoder passwordEncoder;
  private final ObjectMapper objectMapper;

  @PostMapping("/refresh")
  public AuthenticationResponse refeshAuthToken(@CookieValue(name = "${token.refresh-token-cookie-key}") String refreshToken, HttpServletResponse response) throws AuthenticateException {
    // refresh token 유효성 검증
    if (!jwtTokenUtil.validateToken(refreshToken)) { throw new AuthenticateException(EXPIRED_REFRESH_TOKEN); }

    final Claims claims = jwtTokenUtil.extractAllClaims(refreshToken);
    final String userId = claims.getSubject();
    final Set<Role> userRoles = objectMapper.convertValue(claims.get("roles"), new TypeReference<>() {});

    // 필요시, token의 user id를 통한 DB 사용자 인증 절차 추가

    ApplicationUser applicationUser = ApplicationUser.builder().userId(userId).roles(userRoles).build();
    // 새로운 토큰 생성
    String newAccessToken = jwtTokenUtil.generateAccessToken(applicationUser);
    String newRefreshToken = jwtTokenUtil.generateRefreshToken(applicationUser);
    // set cookie for refresh token
    response.addHeader(HttpHeaders.SET_COOKIE, jwtTokenUtil.issueRefreshTokenCookie(newRefreshToken));

    return AuthenticationResponse.builder().accessToken(newAccessToken).user(null).build();
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
      // 인증시도를 위해 임시토큰 생성 후 전달
      new JwtAuthenticationToken(
        authenticationRequest.getUserId(),
        authenticationRequest.getPassword()
      )
    );

    // access token set body
    String accessToken = jwtTokenUtil.generateAccessToken(userAuthentication);
    // refresh token set header cookie
    String refreshToken = jwtTokenUtil.generateRefreshToken(userAuthentication);
    // set cookie for refresh token
    response.addHeader(HttpHeaders.SET_COOKIE, jwtTokenUtil.issueRefreshTokenCookie(refreshToken));

    return AuthenticationResponse.builder().accessToken(accessToken).user((ApplicationUser) userAuthentication.getDetails()).build();
  }

}
