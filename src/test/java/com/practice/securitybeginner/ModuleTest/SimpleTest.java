package com.practice.securitybeginner.ModuleTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleTest {

  @DisplayName("sample test")
  @Test
  void testMethodNameHere() {
    // given
    int actual = 0;

    // when
    int expected = 0;

    // then
    assertEquals(expected, actual);
  }

}
