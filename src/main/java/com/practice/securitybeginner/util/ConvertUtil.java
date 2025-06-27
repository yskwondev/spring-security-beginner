package com.practice.securitybeginner.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConvertUtil {

  private final ObjectMapper objectMapper;

  public Map<String, Object> convertObjectToMap(Object object) {
    return objectMapper.convertValue(object, new TypeReference<Map<String, Object>>() {});
  }

}
