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
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// Spring security authentication exception handler
// 필터 통과 중, Exception 발생시 request에 "exception" attr 담아서 다음 필터로 넘겨줌. -> 최종 인증 절차에서 Exception 발생시, ExceptionTranslationFilter에서 처리
// 인증 필터 진행중 Security Exception 발생시, ExceptionTranslationFilter에서 처리
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

    // 실제 예외 가져오기 (Filter에서 발생한 원본 예외)
    Throwable cause = (Throwable) request.getAttribute("exception");
    if (cause != null) {
      log.warn("Authentication failed with filter exception : {} - {}",
               cause.getClass().getSimpleName(), cause.getMessage());
    } else {
      log.warn("Authentication failed with Security exception : {} - {}",
               authException.getClass().getSimpleName(), authException.getMessage());
    }
    ErrorCode errorCode = defineErrorCode(cause, authException);
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

  private ErrorCode defineErrorCode(Throwable cause, AuthenticationException authException) {
    if (cause instanceof AuthenticateException) {
      return ((AuthenticateException) cause).getErrorCode();
    } else if (cause instanceof JwtException) {  // JWT 관련 예외
      return  ErrorCode.INVALID_TOKEN;
    } else if (cause != null) {
      return ErrorCode.AUTH_ERROR;  // 기본 인증 에러
    } else {
      return mapSpringSecurityException(authException);
    }
  }

  /**
   * Spring Security 예외를 ErrorCode로 매핑
   */
  private ErrorCode mapSpringSecurityException(AuthenticationException authException) {
    String exceptionName = authException.getClass().getSimpleName();
    return switch (exceptionName) {
      case "BadCredentialsException" -> ErrorCode.AUTH_ERROR;
      case "InsufficientAuthenticationException" -> ErrorCode.MISSING_ACCESS_TOKEN;  // 토큰이 없는 경우
      case "AuthenticationCredentialsNotFoundException" -> ErrorCode.MISSING_ACCESS_TOKEN;
      case "AccountExpiredException", "CredentialsExpiredException" -> ErrorCode.EXPIRED_ACCESS_TOKEN;
      case "DisabledException" -> ErrorCode.DISABLED_USER;
      case "LockedException" -> ErrorCode.LOCKED_USER;
      default -> ErrorCode.AUTH_ERROR;  // 기본 인증 에러
    };
  }
}