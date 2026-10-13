package org.example.controller;

import org.example.controller.dto.CreatePostRequest;
import org.example.controller.dto.UpdatePostRequest;
import org.example.domain.Post;
import org.example.exception.PostNotFoundException;
import org.example.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/posts")
public class PostController {
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    // 게시글 작성
    @PostMapping
    public ApiResponse<Post> create(@RequestBody CreatePostRequest request) {
        try {
            return ApiResponse.success("게시글이 작성되었습니다.",
                    service.create(request.title(), request.content(), request.category(), request.author()));
        } catch (IllegalArgumentException e) {
            return ApiResponse.failure("INVALID_INPUT", e.getMessage());
        }
    }

    // 게시글 목록 조회
    @GetMapping
    public ApiResponse<List<Post>> findAll() {

        return ApiResponse.success("게시글 목록을 조회했습니다.", service.findAll());
    }

    // 게시글 단건 조회
    @GetMapping(path = "/{postId}")
    public ApiResponse<Post> findById(@PathVariable(name = "postId") long id) {
        try {
            return ApiResponse.success("게시글을 조회했습니다.", service.findById(id));
        } catch (PostNotFoundException e) {
            return ApiResponse.failure("POST_NOT_FOUND", e.getMessage());
        }
    }

    // 게시글 수정
    @PutMapping(path = "/{postId}")
    public ApiResponse<Post> update(
            @PathVariable(name = "postId") long id,
            @RequestBody UpdatePostRequest request
    ) {
        try {
            return ApiResponse.success("게시글이 수정되었습니다.",
                    service.update(id, request.title(), request.content(), request.category()));
        } catch (PostNotFoundException e) {
            return ApiResponse.failure("POST_NOT_FOUND", e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.failure("INVALID_INPUT", e.getMessage());
        }
    }

    // 게시글 삭제
    @DeleteMapping(path = "/{postId}")
    public ApiResponse<Void> delete(@PathVariable(name = "postId") long id) {
        try {
            service.delete(id);
            return ApiResponse.success("게시글이 삭제되었습니다.", null);
        } catch (PostNotFoundException e) {
            return ApiResponse.failure("POST_NOT_FOUND", e.getMessage());
        }
    }
}
