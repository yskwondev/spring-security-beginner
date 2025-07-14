package com.practice.securitybeginner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.practice.securitybeginner.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class ErrorResponse {

  private String errorCode;
  private String message;

  public static ErrorResponse with(ErrorCode error) {
    return ErrorResponse
      .builder()
      .errorCode(error.getCode())
      .message(error.getMessage())
      .build();
  }
}
