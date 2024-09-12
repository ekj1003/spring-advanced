package org.example.expert.domain.user.service;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUser() {
        // 테스트용 사용자 생성
        user = new User("user@example.com", "2573758Aa", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
    }

    @Test
    public void getUser_성공_유저정보_반환() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        UserResponse response = userService.getUser(1L);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("user@example.com", response.getEmail());
    }

    @Test
    public void getUser_실패_존재하지_않는_유저() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.getUser(1L)
        );
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void changePassword_성공_비밀번호_변경() {
        // given
        UserChangePasswordRequest request = new UserChangePasswordRequest("2573758Aa", "new2573758Aa");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).willReturn(true);
        given(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).willReturn(false);
        given(passwordEncoder.encode(request.getNewPassword())).willReturn("new2573758Aa");

        // when
        userService.changePassword(1L, request);

        // then
        assertEquals("new2573758Aa", user.getPassword());
    }

    @Test
    public void changePassword_실패_비밀번호_형식_불일치() {
        // given
        UserChangePasswordRequest request = new UserChangePasswordRequest("2573758Aa", "short");

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.changePassword(1L, request)
        );
        assertEquals("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", exception.getMessage());
    }

}
