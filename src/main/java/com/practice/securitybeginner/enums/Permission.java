package com.practice.securitybeginner.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {

  CREATE("CREATE_AUTHORITY"),
  READ("READ_AUTHORITY"),
  UPDATE("UPDATE_AUTHORITY"),
  DELETE("DELETE_AUTHORITY");

  private final String permission;

}
