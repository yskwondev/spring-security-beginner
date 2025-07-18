package com.practice.securitybeginner.ModuleTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class SimpleTest {

  @Test
  public void testSimple() {
    String[] simpleArr = new String[10];
    ArrayList<String> list = new ArrayList<String>();

    Assertions.assertEquals(simpleArr instanceof Arrays, list.size());

  }

}
