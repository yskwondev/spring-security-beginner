package com.practice.securitybeginner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.practice.securitybeginner.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommonResponse<T> {

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime serverTime;
  private String method;
  private String path;
  private int status;
  private T data;

  public static <T> CommonResponse<T> success(ServerHttpRequest request, T data) {
    return new CommonResponse<>(LocalDateTime.now(), request.getMethod().name(), request.getURI().getPath(), HttpStatus.OK.value(), data);
  }

  public static CommonResponse<ErrorResponse> fail(ErrorCode error, HttpServletRequest request) {
    return new CommonResponse<>(LocalDateTime.now(), request.getMethod(), request.getRequestURI(), error.getHttpStatus().value(), ErrorResponse.with(error));
  }

}
