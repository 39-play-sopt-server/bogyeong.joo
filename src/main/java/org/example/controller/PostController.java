package org.example.controller;

import org.example.domain.Category;
import org.example.domain.Post;
import org.example.exception.PostNotFoundException;
import org.example.service.PostService;
import java.util.List;

public class PostController {
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    public ApiResponse<Post> create(String title, String content, Category category, String author) {
        try {
            return ApiResponse.success("게시글이 작성되었습니다.", service.create(title, content, category, author));
        } catch (IllegalArgumentException e) {
            return ApiResponse.failure("INVALID_INPUT", e.getMessage());
        }
    }

    public ApiResponse<List<Post>> findAll() {
        return ApiResponse.success("게시글 목록을 조회했습니다.", service.findAll());
    }

    public ApiResponse<Post> findById(long id) {
        try {
            return ApiResponse.success("게시글을 조회했습니다.", service.findById(id));
        } catch (PostNotFoundException e) {
            return ApiResponse.failure("POST_NOT_FOUND", e.getMessage());
        }
    }

    public ApiResponse<Post> update(long id, String title, String content, Category category) {
        try {
            return ApiResponse.success("게시글이 수정되었습니다.", service.update(id, title, content, category));
        } catch (PostNotFoundException e) {
            return ApiResponse.failure("POST_NOT_FOUND", e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.failure("INVALID_INPUT", e.getMessage());
        }
    }

    public ApiResponse<Void> delete(long id) {
        try {
            service.delete(id);
            return ApiResponse.success("게시글이 삭제되었습니다.", null);
        } catch (PostNotFoundException e) {
            return ApiResponse.failure("POST_NOT_FOUND", e.getMessage());
        }
    }
}
