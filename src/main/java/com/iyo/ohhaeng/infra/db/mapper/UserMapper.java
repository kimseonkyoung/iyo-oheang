package com.iyo.ohhaeng.infra.db.mapper;

import com.iyo.ohhaeng.domain.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserMapper {

    void insert(@Param("userId") String userId, @Param("userName") String userName);

    Optional<User> findById(@Param("userId") String userId);

    Optional<User> findByName(@Param("name") String name);

    User findByIdForUpdate(@Param("userId") String userId);

    void update(User user);
}
