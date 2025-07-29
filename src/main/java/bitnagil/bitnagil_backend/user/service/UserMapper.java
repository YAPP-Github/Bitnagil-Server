package bitnagil.bitnagil_backend.user.service;

import org.springframework.stereotype.Component;

import bitnagil.bitnagil_backend.user.domain.User;
import bitnagil.bitnagil_backend.user.response.UserInfoResponse;

/*
 * 유저 관련 DTO로 변환하는 클래스입니다.
 */
@Component
public class UserMapper {

    public UserInfoResponse toUserInfoResponse(User user) {
        return UserInfoResponse.builder()
            .nickname(user.getNickname())
            .build();
    }
}
