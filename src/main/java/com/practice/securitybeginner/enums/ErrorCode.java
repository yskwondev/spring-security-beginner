package com.practice.securitybeginner.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // common exception
  SERVER_ERROR(INTERNAL_SERVER_ERROR, "COMM-000", "unexpected error"),
  RESOURCE_NOT_FOUND(NOT_FOUND, "COMM-001", "not found server resources"),
  NOT_READABLE_ARGUMENT(BAD_REQUEST, "COMM-002", "invalid api arguments"),
  ILLEGAL_ARGUMENT(BAD_REQUEST, "COMM-003", "illegal arguments"),

  // user exception
  USER_NOT_FOUND(UNAUTHORIZED, "COMM-010", "not found user"),
  DISABLED_USER(UNAUTHORIZED, "COMM-011", "account is disabled"),
  LOCKED_USER(UNAUTHORIZED, "COMM-012", "account is locked"),
  DUPLICATE_EMAIL(BAD_REQUEST, "COMM-013", "email already exists"),

  // auth exception
  AUTH_ERROR(UNAUTHORIZED, "AUTH-000", "authentication failed"),
  INVALID_TOKEN(UNAUTHORIZED, "AUTH-001", "invalid token"),
  EXPIRED_ACCESS_TOKEN(UNAUTHORIZED, "AUTH-002", "access token expired"),
  MISSING_ACCESS_TOKEN(UNAUTHORIZED, "AUTH-003", "missing access token"),
  EXPIRED_REFRESH_TOKEN(UNAUTHORIZED, "AUTH-004", "refresh token expired"),
  MISSING_REFRESH_TOKEN(UNAUTHORIZED, "AUTH-005", "missing refresh token");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

}
