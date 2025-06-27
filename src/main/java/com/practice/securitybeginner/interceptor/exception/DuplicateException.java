package com.practice.securitybeginner.interceptor.exception;

import com.practice.securitybeginner.enums.ErrorCode;

public class DuplicateException extends AbstractBusinessException {
  public DuplicateException(ErrorCode errorCode) {
    super(errorCode);
  }
}
