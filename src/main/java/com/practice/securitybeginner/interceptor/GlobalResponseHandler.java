package com.practice.securitybeginner.interceptor;

import com.practice.securitybeginner.dto.CommonResponse;
import com.practice.securitybeginner.dto.ErrorResponse;
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
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

import static com.practice.securitybeginner.enums.ErrorCode.*;

// Dispatcher Servlet 내의 전체 response 및 오류 처리
@Slf4j
@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

  // todo application 전체 에러처리를 위해 에러마다의 http header status를 정의해주어야 하는데,, 이게 맞나??

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

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResourceException(NoResourceFoundException ex, HttpServletRequest request) {
    ErrorResponse response = new ErrorResponse(LocalDateTime.now(), request.getMethod(), request.getRequestURI(), RESOURCE_NOT_FOUND.getHttpStatus().value(), RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getMessage());
    return ResponseEntity.status(RESOURCE_NOT_FOUND.getHttpStatus()).body(response);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public  ResponseEntity<ErrorResponse> handleNotReadableMessageException(HttpMessageNotReadableException ex, HttpServletRequest request) {
    ErrorResponse response = new ErrorResponse(LocalDateTime.now(), request.getMethod(), request.getRequestURI(), NOT_READABLE_ARGUMENT.getHttpStatus().value(), NOT_READABLE_ARGUMENT.getCode(), NOT_READABLE_ARGUMENT.getMessage());
    return ResponseEntity.status(NOT_READABLE_ARGUMENT.getHttpStatus()).body(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public  ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
    ErrorResponse response = new ErrorResponse(LocalDateTime.now(), request.getMethod(), request.getRequestURI(), ILLEGAL_ARGUMENT.getHttpStatus().value(), ILLEGAL_ARGUMENT.getCode(), ILLEGAL_ARGUMENT.getMessage());
    return ResponseEntity.status(ILLEGAL_ARGUMENT.getHttpStatus()).body(response);
  }

  // Spring MVC에서 정의된 Exception은 Custom Handler를 구현한다.
  @ExceptionHandler(MissingRequestCookieException.class)
  public  ResponseEntity<ErrorResponse> handleMissingCookieException(MissingRequestCookieException ex, HttpServletRequest request) {
    AuthenticateException authException = new AuthenticateException(MISSING_REFRESH_TOKEN);
    return generateErrorResponse(authException, request);
  }

  // 그 외 정의되지 않은 모든 Exception들을 받아주는 handler
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleExceptions(Exception ex, HttpServletRequest request) {
    log.error("[ UNEXPECTED EXCEPTION OCCURED ]", ex);
    ErrorResponse response = new ErrorResponse(LocalDateTime.now(), request.getMethod(), request.getRequestURI(), SERVER_ERROR.getHttpStatus().value(), SERVER_ERROR.getCode(), SERVER_ERROR.getMessage());
    return ResponseEntity.status(SERVER_ERROR.getHttpStatus()).body(response);
  }

  private ResponseEntity<ErrorResponse> generateErrorResponse(AbstractCommonException ex, HttpServletRequest request) {
    log.error("[ ERROR EXCEPTION OCCURED ]", ex);
    ErrorResponse response = ErrorResponse.error(
      ex.getErrorCode(),
      request.getMethod(),
      request.getRequestURI()
    );
    return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(response);
  }

}
