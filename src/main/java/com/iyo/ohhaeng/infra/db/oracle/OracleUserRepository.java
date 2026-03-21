package com.iyo.ohhaeng.infra.db.oracle;

import com.iyo.ohhaeng.domain.user.User;
import com.iyo.ohhaeng.infra.db.UserRepository;
import com.iyo.ohhaeng.infra.db.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OracleUserRepository implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public Optional<User> findById(String userId) {
        return userMapper.findById(userId);
    }

    @Override
    public Optional<User> findByName(String name) {
        return userMapper.findByName(name);
    }

    @Override
    public User findByIdForUpdate(String userId) {
        return LockingSupport.execute(() -> userMapper.findByIdForUpdate(userId));
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
    }
}
