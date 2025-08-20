package com.practice.securitybeginner.interceptor;

import com.practice.securitybeginner.dto.CommonResponse;
import com.practice.securitybeginner.dto.ErrorResponse;
import com.practice.securitybeginner.enums.ErrorCode;
import com.practice.securitybeginner.interceptor.exception.AbstractBusinessException;
import com.practice.securitybeginner.interceptor.exception.AbstractCommonException;
import com.practice.securitybeginner.interceptor.exception.AuthenticateException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

// Dispatcher Servlet 내의 전체 response 및 오류 처리
@Slf4j
@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
    // wrapper를 사용할지 말지를 결정함.
    // 전체 허용시, Controller에서 String 타입을 리턴할 경우에 ClassCastException 발생함
    // String은 String 그대로 리턴
    return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
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

    if (body instanceof CommonResponse<?> commonResponse) {
      // response의 body에 error 객체가 들어있다면, Exception 발생. 이에 따라 응답헤더에 Exception에 맞는 Status 삽입
      if (commonResponse.getData() instanceof ErrorResponse) {
        response.setStatusCode(HttpStatusCode.valueOf(commonResponse.getStatus()));
      }
      return body;
    }

    // todo :: 파일 등 리소스에 대한 리턴 래핑 확인 필요함

    // 그 외의 경우 ApiResponse로 래핑
    return CommonResponse.success(request, body);
  }

  // 비즈니스 로직 내에서 Exception으로 처리하는 사항들은 AbstractBusinessException을 상속하여 Custom Exception으로 생성한다.
  @ExceptionHandler(AbstractBusinessException.class)
  public  CommonResponse<ErrorResponse> handleBusinessException(AbstractBusinessException ex, HttpServletRequest request) {
    return generateErrorResponse(ex, request);
  }

  // Spring MVC에서 정의된 Exception은 Custom Handler를 구현한다.
  @ExceptionHandler(NoResourceFoundException.class)
  public CommonResponse<ErrorResponse> handleNoResourceException(NoResourceFoundException ex, HttpServletRequest request) {
    return generateErrorResponse(ex, RESOURCE_NOT_FOUND, request);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public CommonResponse<ErrorResponse> handleNotReadableMessageException(HttpMessageNotReadableException ex, HttpServletRequest request) {
    return generateErrorResponse(ex, NOT_READABLE_ARGUMENT, request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public CommonResponse<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
    return generateErrorResponse(ex, ILLEGAL_ARGUMENT, request);
  }

  @ExceptionHandler(MissingRequestCookieException.class)
  public CommonResponse<ErrorResponse> handleMissingCookieException(MissingRequestCookieException ex, HttpServletRequest request) {
    AuthenticateException authException = new AuthenticateException(MISSING_REFRESH_TOKEN);
    return generateErrorResponse(authException, request);
  }

  @ExceptionHandler({
    DataAccessException.class,
    MyBatisSystemException.class
  })
  public CommonResponse<ErrorResponse> handleDatabaseException(Exception ex, HttpServletRequest request) {
    Throwable cause = ex.getCause().getCause(); // mybatis exception unwrapping
    // 특정할만한 에러 발생시 추가
    if (cause instanceof CannotGetJdbcConnectionException) {
      return generateErrorResponse(ex, DB_CONNECTION_FAILED, request);
    }
    return generateErrorResponse(ex, DB_ERROR, request); // client단에서 db에러를 어떻게 표현해야 좋을까
  }

  // 그 외 정의되지 않은 모든 Exception들을 받아주는 handler
  @ExceptionHandler(Exception.class)
  public CommonResponse<ErrorResponse> handleExceptions(Exception ex, HttpServletRequest request) {
    log.error("Exception type : {}", ex.getClass().getName());
    return generateErrorResponse(ex, SERVER_ERROR, request);
  }

  private CommonResponse<ErrorResponse> generateErrorResponse(AbstractCommonException ex, HttpServletRequest request) {
    log.error("[ BUSINESS EXCEPTION OCCURED ]", ex);
    return CommonResponse.fail(ex.getErrorCode(), request);
  }

  private CommonResponse<ErrorResponse> generateErrorResponse(Exception ex, ErrorCode errorCode, HttpServletRequest request) {
    log.error("[ SERVER EXCEPTION OCCURED ]", ex);
    return CommonResponse.fail(errorCode, request);
  }

}
