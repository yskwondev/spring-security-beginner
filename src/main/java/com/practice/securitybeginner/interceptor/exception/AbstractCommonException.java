package com.practice.securitybeginner.interceptor.exception;

import com.practice.securitybeginner.enums.ErrorCode;
import lombok.Getter;

@Getter
public abstract class AbstractCommonException extends RuntimeException {

  private final ErrorCode errorCode;

  public AbstractCommonException(final ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

}
