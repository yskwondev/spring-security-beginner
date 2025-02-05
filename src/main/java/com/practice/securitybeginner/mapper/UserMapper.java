package com.practice.securitybeginner.mapper;

import com.practice.securitybeginner.domain.ApplicationUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {

  Optional<ApplicationUser> selectUserById(String userId);

}
