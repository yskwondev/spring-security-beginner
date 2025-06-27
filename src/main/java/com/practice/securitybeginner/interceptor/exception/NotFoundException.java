package com.practice.securitybeginner.interceptor.exception;

import com.practice.securitybeginner.enums.ErrorCode;

public class NotFoundException extends AbstractBusinessException {
  public NotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }
}
