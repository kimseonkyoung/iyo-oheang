package com.iyo.ohhaeng.infra.db.mapper;

import com.iyo.ohhaeng.domain.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserMapper {

    Optional<User> findById(@Param("userId") String userId);

    Optional<User> findByName(@Param("name") String name);

    User findByIdForUpdate(@Param("userId") String userId);

    void update(User user);
}
