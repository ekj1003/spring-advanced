package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private TodoService todoService;

    @Test
    public void saveTodo_성공_응답을_반환한다() {
        // given
        AuthUser authUser = new AuthUser(1L, "user@example.com", UserRole.USER);
        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("Title", "Contents");
        User user = new User("user@example.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        String weather = "Sunny";
        Todo savedTodo = new Todo("Title", "Contents", weather, user);
        ReflectionTestUtils.setField(savedTodo, "id", 1L);
        given(weatherClient.getTodayWeather()).willReturn(weather);
        given(todoRepository.save(any(Todo.class))).willReturn(savedTodo);

        // when
        TodoSaveResponse response = todoService.saveTodo(authUser, todoSaveRequest);


        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Title", response.getTitle());
        assertEquals("Contents", response.getContents());
        assertEquals("Sunny", response.getWeather());
        assertEquals(1L, response.getUser().getId());
        assertEquals("user@example.com", response.getUser().getEmail());
    }

    @Test
    public void todo_목록_조회에_성공한다() {
        // given
        User user = new User("user@example.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        Todo todo = new Todo("Title", "Contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        Page<Todo> todoPage = new PageImpl<>(Collections.singletonList(todo), PageRequest.of(0, 10), 1);

        given(todoRepository.findAllByOrderByModifiedAtDesc(any(Pageable.class))).willReturn(todoPage);

        // when
        Page<TodoResponse> responsePage = todoService.getTodos(1, 10);

        // then
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        assertEquals(1L, responsePage.getContent().get(0).getId());
        assertEquals("Title", responsePage.getContent().get(0).getTitle());
        assertEquals("Contents", responsePage.getContent().get(0).getContents());
        assertEquals("Sunny", responsePage.getContent().get(0).getWeather());
    }

    @Test
    public void getTodo_존재하지_않는_Todo_조회_예외를_던진다() {
        // given
        long todoId = 1L;
        given(todoRepository.findByIdWithUser(todoId)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                todoService.getTodo(todoId)
        );
        assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    public void getTodo_성공_응답을_반환한다() {
        // given
        long todoId = 1L;
        User user = new User("user@example.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        Todo todo = new Todo("Title", "Contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        given(todoRepository.findByIdWithUser(todoId)).willReturn(Optional.of(todo));

        // when
        TodoResponse response = todoService.getTodo(todoId);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Title", response.getTitle());
        assertEquals("Contents", response.getContents());
        assertEquals("Sunny", response.getWeather());
        assertEquals(1L, response.getUser().getId());
        assertEquals("user@example.com", response.getUser().getEmail());
    }
}
