package com.practice.securitybeginner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.practice.securitybeginner.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {

  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime serverTime;
  private String method;
  private String path;
  private int status;
  private String errorCode;
  private String message;

  public static ErrorResponse error(ErrorCode error, String method, String apiPath) {
      return new ErrorResponse(LocalDateTime.now(), method, apiPath, error.getHttpStatus().value(), error.getCode(), error.getMessage());
  }

}
