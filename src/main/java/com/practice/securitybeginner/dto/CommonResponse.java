package com.practice.securitybeginner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommonResponse<T> {

  private LocalDateTime servertime;
  private String method;
  private String path;
  private T data;
  private int status;
  private String message;

  public static <T> CommonResponse<T> success(String method, String apiPath, T data) {
      return new CommonResponse<>(LocalDateTime.now(), method, apiPath, data, HttpStatus.OK.value(), "success");
  }

}
