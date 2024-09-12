package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class CommentAdminServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentAdminService commentAdminService;

    @Test
    public void deleteComment_정상_삭제() {
        // given
        long commentId = 1L;

        // when
        commentAdminService.deleteComment(commentId);

        // then
        // deleteById가 정상적으로 호출되었는지 확인
        verify(commentRepository, times(1)).deleteById(commentId);
    }

}