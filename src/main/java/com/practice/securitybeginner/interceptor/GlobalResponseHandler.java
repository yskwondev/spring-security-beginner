package com.practice.securitybeginner.interceptor;

import com.practice.securitybeginner.dto.CommonResponse;
import com.practice.securitybeginner.dto.ErrorResponse;
import com.practice.securitybeginner.enums.ErrorCode;
import com.practice.securitybeginner.interceptor.exception.AbstractBusinessException;
import com.practice.securitybeginner.interceptor.exception.AbstractCommonException;
import com.practice.securitybeginner.interceptor.exception.AuthenticateException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static com.practice.securitybeginner.enums.ErrorCode.*;

// Dispatcher Servlet 내의 전체 response 및 오류 처리
@Slf4j
@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
    // wrapper를 사용할지 말지를 결정함.
    return true;
  }

  @Override
  public Object beforeBodyWrite(
    Object body,
    @NonNull MethodParameter returnType,
    @NonNull MediaType selectedContentType,
    @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
    @NonNull ServerHttpRequest request,
    @NonNull ServerHttpResponse response
  ) {
    // ApiResponse로 이미 래핑된 경우 그대로 반환
    if (body instanceof CommonResponse || body instanceof ErrorResponse) {
        return body;
    }
    // 그 외의 경우 ApiResponse로 래핑
    return CommonResponse.success(request.getMethod().name(), request.getURI().getPath(), body);
  }

  // 비즈니스 로직 내에서 Exception으로 처리하는 사항들은 AbstractBusinessException을 상속하여 Custom Exception으로 생성한다.
  @ExceptionHandler(AbstractBusinessException.class)
  public  ResponseEntity<ErrorResponse> handleBusinessException(AbstractBusinessException ex, HttpServletRequest request) {
    return generateErrorResponse(ex, request);
  }

  // Spring MVC에서 정의된 Exception은 Custom Handler를 구현한다.
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResourceException(NoResourceFoundException ex, HttpServletRequest request) {
    return generateErrorResponse(ex, RESOURCE_NOT_FOUND, request);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadableMessageException(HttpMessageNotReadableException ex, HttpServletRequest request) {
    return generateErrorResponse(ex, NOT_READABLE_ARGUMENT, request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
    return generateErrorResponse(ex, ILLEGAL_ARGUMENT, request);
  }

  @ExceptionHandler(MissingRequestCookieException.class)
  public ResponseEntity<ErrorResponse> handleMissingCookieException(MissingRequestCookieException ex, HttpServletRequest request) {
    AuthenticateException authException = new AuthenticateException(MISSING_REFRESH_TOKEN);
    return generateErrorResponse(authException, request);
  }

  @ExceptionHandler(CannotGetJdbcConnectionException.class)
  public ResponseEntity<ErrorResponse> handleMissingJdbcConnectionException(CannotGetJdbcConnectionException ex, HttpServletRequest request) {
    return generateErrorResponse(ex, DB_CONNECTION_FAILED, request);
  }

  // 그 외 정의되지 않은 모든 Exception들을 받아주는 handler
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleExceptions(Exception ex, HttpServletRequest request) {
    return generateErrorResponse(ex, SERVER_ERROR, request);
  }

  private ResponseEntity<ErrorResponse> generateErrorResponse(AbstractCommonException ex, HttpServletRequest request) {
    log.error("[ BUSINESS EXCEPTION OCCURED ]", ex);
    ErrorResponse response = ErrorResponse.error(
      ex.getErrorCode(),
      request.getMethod(),
      request.getRequestURI()
    );
    return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(response);
  }

  private ResponseEntity<ErrorResponse> generateErrorResponse(Exception ex, ErrorCode errorCode, HttpServletRequest request) {
    log.error("[ SERVER EXCEPTION OCCURED ]", ex);
    ErrorResponse response = ErrorResponse.error(errorCode, request.getMethod(), request.getRequestURI());
    return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
  }


}
