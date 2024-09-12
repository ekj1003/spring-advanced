package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    public void signup_이메일이_이미_존재하는_경우_예외를_던진다() {
        // given
        SignupRequest signupRequest = new SignupRequest("test@example.com", "password", "USER");
        given(userRepository.existsByEmail(signupRequest.getEmail())).willReturn(true);

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                authService.signup(signupRequest)
        );
        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
    }

    @Test
    public void signup_성공_토큰을_반환한다() {
        // given
        SignupRequest signupRequest = new SignupRequest("test@example.com", "password", "USER");
        String encodedPassword = "encodedPassword";
        UserRole userRole = UserRole.USER;
        User newUser = new User(signupRequest.getEmail(), encodedPassword, userRole);
        User savedUser = new User(signupRequest.getEmail(), encodedPassword, userRole);
        String token = "jwtToken";

        given(userRepository.existsByEmail(signupRequest.getEmail())).willReturn(false);
        given(passwordEncoder.encode(signupRequest.getPassword())).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), savedUser.getUserRole())).willReturn(token);

        // when
        SignupResponse response = authService.signup(signupRequest);

        // then
        assertNotNull(response);
        assertEquals("jwtToken", response.getBearerToken());
    }

    @Test
    public void signin_이메일을_찾을_수_없는_경우_예외를_던진다() {
        // given
        SigninRequest signinRequest = new SigninRequest("test@example.com", "password");
        given(userRepository.findByEmail(signinRequest.getEmail())).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                authService.signin(signinRequest)
        );
        assertEquals("가입되지 않은 유저입니다.", exception.getMessage());
    }

    @Test
    public void signin_비밀번호가_일치하지_않는_경우_예외를_던진다() {
        // given
        SigninRequest signinRequest = new SigninRequest("test@example.com", "password");
        User user = new User("test@example.com", "encodedPassword", UserRole.USER);

        given(userRepository.findByEmail(signinRequest.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())).willReturn(false);

        // when & then
        AuthException exception = assertThrows(AuthException.class, () ->
                authService.signin(signinRequest)
        );
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }

    @Test
    public void signin_성공_토큰을_반환한다() {
        // given
        SigninRequest signinRequest = new SigninRequest("test@example.com", "password");
        User user = new User("test@example.com", "encodedPassword", UserRole.USER);
        String token = "jwtToken";

        given(userRepository.findByEmail(signinRequest.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())).willReturn(true);
        given(jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole())).willReturn(token);

        // when
        SigninResponse response = authService.signin(signinRequest);

        // then
        assertNotNull(response);
        assertEquals("jwtToken", response.getBearerToken());
    }
}
