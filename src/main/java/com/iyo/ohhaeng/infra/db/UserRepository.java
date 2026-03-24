package com.iyo.ohhaeng.infra.db;

import com.iyo.ohhaeng.domain.user.User;

import java.util.Optional;

public interface UserRepository {
    void insert(String userId, String userName);

    Optional<User> findById(String userId);
    Optional<User> findByName(String name);
    User findByIdForUpdate(String userId);
    void update(User user);
}
