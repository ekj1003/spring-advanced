package org.example.expert.domain.user.service;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
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
public class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAdminService userAdminService;

    private User user;
    @BeforeEach
    public void setUser() {
        user = new User("user@naver.com", "password1234", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
    }
    @Test
    public void changeUserRole_역할_변경_성공() {
        // given
        UserRoleChangeRequest request = new UserRoleChangeRequest("ADMIN");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        userAdminService.changeUserRole(1L, request);

        // then
        assertEquals(UserRole.ADMIN, user.getUserRole());
    }

    @Test
    public void changeUserRole_역할_변경_실패_존재하지_않는_사용자() {
        // given
        UserRoleChangeRequest request = new UserRoleChangeRequest("ADMIN");
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userAdminService.changeUserRole(1L, request)
        );
        assertEquals("User not found", exception.getMessage());
    }

}
