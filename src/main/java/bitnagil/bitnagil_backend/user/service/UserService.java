package bitnagil.bitnagil_backend.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bitnagil.bitnagil_backend.user.domain.User;
import bitnagil.bitnagil_backend.user.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;

/**
 * 유저 정보를 관리하는 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(User user) {
        return userMapper.toUserInfoResponse(user);
    }
}
