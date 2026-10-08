package org.example;

import org.example.controller.ApiResponse;
import org.example.controller.PostController;
import org.example.domain.Category;
import org.example.domain.Post;
import org.example.exception.PostNotFoundException;
import org.example.repository.InMemoryPostRepository;
import org.example.service.PostIdGenerator;
import org.example.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PostBoardTest {
    private PostService service;
    private PostController controller;

    @BeforeEach
    void setUp() {
        service = new PostService(new InMemoryPostRepository(), new PostIdGenerator());
        controller = new PostController(service);
    }

    @Test
    void invalidInputDoesNotSavePost() {
        String[] emptyValues = {null, "", "   ", "\t\n"};
        for (String value : emptyValues) {
            assertFalse(controller.create(value, "본문", Category.DAILY, "보경").isSuccess());
            assertFalse(controller.create("제목", value, Category.DAILY, "보경").isSuccess());
            assertFalse(controller.create("제목", "본문", Category.DAILY, value).isSuccess());
        }
        ApiResponse<Post> response = controller.create("제목", "본문", null, "보경");
        assertEquals("INVALID_INPUT", response.getCode());
        assertNull(response.getData());
        assertTrue(service.findAll().isEmpty());
    }

    @Test
    void deletingPostDoesNotChangeOrReuseOtherIds() {
        Post first = service.create("첫 글", "본문", Category.DAILY, "보경");
        Post second = service.create("두 번째", "본문", Category.QUESTION, "보경");
        service.delete(first.getId());
        Post third = service.create("세 번째", "본문", Category.INFORMATION, "보경");

        assertSame(second, service.findById(second.getId()));
        assertTrue(third.getId() > second.getId());
        assertEquals(List.of(second, third), service.findAll());
    }

    @Test
    void failedUpdateKeepsOriginalPost() {
        Post original = service.create("제목", "본문", Category.DAILY, "보경");
        ApiResponse<Post> response = controller.update(original.getId(), "새 제목", " ", Category.QUESTION);

        assertEquals("INVALID_INPUT", response.getCode());
        assertSame(original, service.findById(original.getId()));
        assertEquals("제목", original.getTitle());
        assertEquals("본문", original.getContent());
        assertEquals(Category.DAILY, original.getCategory());
        assertNull(original.getUpdatedAt());
    }

    @Test
    void invalidUpdateAfterSuccessfulUpdateKeepsAllFields() {
        Post post = service.create("제목", "본문", Category.DAILY, "보경");
        service.update(post.getId(), "수정 제목", "수정 본문", Category.QUESTION);
        LocalDateTime updatedAt = post.getUpdatedAt();

        assertEquals("INVALID_INPUT", controller.update(post.getId(), " ", "다른 본문", Category.DAILY).getCode());
        assertEquals("INVALID_INPUT", controller.update(post.getId(), "다른 제목", " ", Category.DAILY).getCode());
        assertEquals("INVALID_INPUT", controller.update(post.getId(), "다른 제목", "다른 본문", null).getCode());

        assertEquals("수정 제목", post.getTitle());
        assertEquals("수정 본문", post.getContent());
        assertEquals(Category.QUESTION, post.getCategory());
        assertEquals(updatedAt, post.getUpdatedAt());
    }

    @Test
    void updatePreservesIdentityAndCreationInformation() {
        Post original = service.create("제목", "본문", Category.DAILY, "보경");
        long originalId = original.getId();
        String originalAuthor = original.getAuthor();
        LocalDateTime originalCreatedAt = original.getCreatedAt();
        Post updated = service.update(original.getId(), "수정 제목", "수정 본문", Category.QUESTION);

        assertSame(original, updated);
        assertEquals(originalId, updated.getId());
        assertEquals(originalAuthor, updated.getAuthor());
        assertEquals(originalCreatedAt, updated.getCreatedAt());
        assertEquals("수정 제목", updated.getTitle());
        assertEquals("수정 본문", updated.getContent());
        assertEquals(Category.QUESTION, updated.getCategory());
        assertNotNull(updated.getUpdatedAt());
        assertSame(updated, service.findById(originalId));
    }

    @Test
    void missingPostThrowsExceptionAndControllerConvertsItToResponse() {
        assertThrows(PostNotFoundException.class, () -> service.findById(100));
        assertThrows(PostNotFoundException.class, () -> service.update(100, "제목", "본문", Category.DAILY));
        assertThrows(PostNotFoundException.class, () -> service.delete(100));
        assertEquals("POST_NOT_FOUND", controller.findById(100).getCode());
        assertEquals("POST_NOT_FOUND", controller.update(100, "제목", "본문", Category.DAILY).getCode());
        assertEquals("POST_NOT_FOUND", controller.delete(100).getCode());
    }

    @Test
    void returnedListCannotChangeStorage() {
        service.create("제목", "본문", Category.DAILY, "보경");
        service.findAll().clear();
        assertEquals(1, service.findAll().size());
    }

    @Test
    void successfulDeleteReturnsCommonResponseWithoutData() {
        Post post = controller.create("제목", "본문", Category.DAILY, "보경").getData();
        ApiResponse<Void> response = controller.delete(post.getId());
        assertTrue(response.isSuccess());
        assertEquals("SUCCESS", response.getCode());
        assertNull(response.getData());
        assertTrue(controller.findAll().getData().isEmpty());
    }
}
