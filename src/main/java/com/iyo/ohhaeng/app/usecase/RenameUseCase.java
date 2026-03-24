package com.iyo.ohhaeng.app.usecase;

import com.iyo.ohhaeng.infra.db.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class RenameUseCase {

    private final UserRepository userRepository;

    @Transactional
    public String execute(String userId, String newName) {
        userRepository.updateName(userId, newName);
        return "이름이 [" + newName + "]으로 변경되었습니다.";
    }
}
