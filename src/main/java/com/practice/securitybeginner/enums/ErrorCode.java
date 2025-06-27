package com.practice.securitybeginner.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // common error
  SERVER_ERROR(INTERNAL_SERVER_ERROR, "COMM-000", "unexpected error"),

  // auth exception code
  INVALID_TOKEN(UNAUTHORIZED, "AUTH-000", "invalid token"),
  EXPIRED_ACCESS_TOKEN(UNAUTHORIZED, "AUTH-001", "access token expired"),
  MISSING_ACCESS_TOKEN(UNAUTHORIZED, "AUTH-002", "missing access token"),
  EXPIRED_REFRESH_TOKEN(UNAUTHORIZED, "AUTH-003", "refresh token expired"),
  MISSING_REFRESH_TOKEN(UNAUTHORIZED, "AUTH-004", "missing refresh token"),
  DISABLED_USER(UNAUTHORIZED, "AUTH-005", "account is disabled"),
  LOCKED_USER(UNAUTHORIZED, "AUTH-006", "account is locked"),
  DUPLICATE_EMAIL(CONFLICT, "AUTH-007", "email already exists"),
  USER_NOT_FOUND(NOT_FOUND, "AUTH-008", "user not found");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

}
