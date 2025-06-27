package com.practice.securitybeginner.mapper;

import com.practice.securitybeginner.domain.ApplicationUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {

  Optional<ApplicationUser> findUserById(String userId);

  void createUser(ApplicationUser user);

  void createUserRoles(ApplicationUser user);

  void updateLastLoginDate(ApplicationUser user);

}
