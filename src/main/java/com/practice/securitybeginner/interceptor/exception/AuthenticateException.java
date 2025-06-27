package com.practice.securitybeginner.interceptor.exception;

import com.practice.securitybeginner.enums.ErrorCode;

public class AuthenticateException extends AbstractBusinessException {
  public AuthenticateException(ErrorCode errorCode) {
    super(errorCode);
  }
}
