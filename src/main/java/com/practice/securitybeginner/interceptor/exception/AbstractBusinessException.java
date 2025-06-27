package com.practice.securitybeginner.interceptor.exception;

import com.practice.securitybeginner.enums.ErrorCode;
import lombok.Getter;

@Getter
public abstract class AbstractBusinessException extends AbstractCommonException {

  public AbstractBusinessException(final ErrorCode errorCode) {
    super(errorCode);
  }

}
