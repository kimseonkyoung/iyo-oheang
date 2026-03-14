package com.iyo.ohhaeng.infra.db;

import com.iyo.ohhaeng.domain.user.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(String userId);
    User findByIdForUpdate(String userId);
    User findByNameForUpdate(String name);
    void update(User user);
}
