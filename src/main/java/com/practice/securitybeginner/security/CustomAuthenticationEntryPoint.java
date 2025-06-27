package com.practice.securitybeginner.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.securitybeginner.dto.ErrorResponse;
import com.practice.securitybeginner.enums.ErrorCode;
import com.practice.securitybeginner.interceptor.exception.AuthenticateException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(HttpServletRequest request,
                       HttpServletResponse response,
                       AuthenticationException authException) throws IOException {

    // 실제 예외 가져오기 (Filter에서 발생한 원본 예외)
    Throwable cause = (Throwable) request.getAttribute("exception");

    if (cause != null) {
      log.error("Authentication error: {}", cause.getMessage());
    }

    ErrorCode errorCode;
    if (cause instanceof AuthenticateException) {
      errorCode = ((AuthenticateException) cause).getErrorCode();
    } else if (cause instanceof JwtException) {  // JWT 관련 예외
      errorCode = ErrorCode.INVALID_TOKEN;
    } else {
      errorCode = ErrorCode.USER_NOT_FOUND;  // 기본 인증 에러
    }

    ErrorResponse errorResponse = ErrorResponse.error(
      errorCode,
      request.getMethod(),
      request.getRequestURI()
    );

    response.setStatus(errorCode.getHttpStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    // response body에 직접 쓰기
    objectMapper.writeValue(response.getWriter(), errorResponse);

  }
}