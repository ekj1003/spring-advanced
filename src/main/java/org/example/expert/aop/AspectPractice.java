package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class AspectPractice {

    // Pointcut
    // 1. CommentAdminController의 deleteComment 메서드 Pointcut으로 지정
    @Pointcut("execution(* org.example.expert.domain.comment.controller.CommentAdminController.deleteComment(..))")
    public void logDeleteCommentAccess(){}

    // 2. UserAdminController의 changeUserRole 메서드 Pointcut으로 지정
    @Pointcut("execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    public void logChangeUserRoleAccess(){}

    // Advice
    // 1. joinPoint(deleteComment 메서드)에 해당 로그를 찍어냄.
    @Before("logDeleteCommentAccess()")
    public void beforeDeleteComment() {
        logAdminAccess();
    }

    // 2. joinPoint(changeUserRole 메서드)에 해당 로그를 찍어냄.
    @Before("logChangeUserRoleAccess()")
    public void beforeChangeUserRole() {
        logAdminAccess();
    }


    // 공통적으로 찍어내는 로그
    public void logAdminAccess() {
        // 현재 요청의 HttpServletRequest를 RequestContextHolder를 통해 가져옴
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attributes.getRequest();

        // 요청한 사용자의 ID 가져오기 (Principal)
        Long userId = (Long) request.getAttribute("userId"); // userId 가져오기
        String requestUrl = request.getRequestURI(); // API 요청 URL
        LocalDateTime requestTime = LocalDateTime.now(); // API 요청 시각

        // 로그 출력
        log.info("Admin access log - userID: {}, requestTime: {}, requestURL: {}", userId, requestTime, requestUrl);
    }

}
