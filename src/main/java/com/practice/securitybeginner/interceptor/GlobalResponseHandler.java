package com.practice.securitybeginner.interceptor;

import com.practice.securitybeginner.dto.CommonResponse;
import com.practice.securitybeginner.dto.ErrorResponse;
import com.practice.securitybeginner.interceptor.exception.AbstractBusinessException;
import com.practice.securitybeginner.interceptor.exception.AbstractCommonException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.LocalDateTime;

import static com.practice.securitybeginner.enums.ErrorCode.MISSING_REFRESH_TOKEN;
import static com.practice.securitybeginner.enums.ErrorCode.SERVER_ERROR;

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

  @ExceptionHandler(AbstractBusinessException.class)
  public  ResponseEntity<ErrorResponse> handleBusinessException(AbstractBusinessException ex, HttpServletRequest request) {
    return generateErrorResponse(ex, request);
  }

  @ExceptionHandler(MissingRequestCookieException.class)
  public  ResponseEntity<ErrorResponse> handleMissingCookieException(Exception ex, HttpServletRequest request) {
    log.error("[ MISSING REFRESH COOKIE EXCEPTION OCCURED ]", ex);
    ErrorResponse response = new ErrorResponse(LocalDateTime.now(), request.getMethod(), request.getRequestURI(), MISSING_REFRESH_TOKEN.getHttpStatus().value(), MISSING_REFRESH_TOKEN.getCode(), MISSING_REFRESH_TOKEN.getMessage());
    return ResponseEntity.status(MISSING_REFRESH_TOKEN.getHttpStatus().value()).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleExceptions(Exception ex, HttpServletRequest request) {
    log.error("[ UNEXPECTED EXCEPTION OCCURED ]", ex);
    ErrorResponse response = new ErrorResponse(LocalDateTime.now(), request.getMethod(), request.getRequestURI(), SERVER_ERROR.getHttpStatus().value(), SERVER_ERROR.getCode(), SERVER_ERROR.getMessage());
    return ResponseEntity.status(SERVER_ERROR.getHttpStatus().value()).body(response);
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
