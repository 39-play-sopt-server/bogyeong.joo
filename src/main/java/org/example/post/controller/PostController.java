package org.example.post.controller;

import org.example.post.controller.dto.CreatePostRequest;
import org.example.post.controller.dto.UpdatePostRequest;
import org.example.post.entity.enums.Category;
import org.example.post.entity.Post;
import org.example.post.controller.docs.PostControllerDocs;
import org.example.global.apiPayload.ApiResponse;
import org.example.post.service.PostService;
import org.example.post.response.code.PostSuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.example.post.controller.dto.PostListResponse;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/posts", produces = "application/json")
public class PostController implements PostControllerDocs {
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    // 게시글 작성
    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<Post>> create(@RequestBody CreatePostRequest request) {
        Post post = service.create(request.title(), request.content(), request.category(), request.memberId());
        return ApiResponse.toResponseEntity(PostSuccessCode.POST_CREATED, post);
    }

    // 게시글 목록 조회
    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<PostListResponse>> findAll(
            @RequestParam(name = "cursor", required = false) String cursor,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "category", required = false) Category category
    ) {
        return ApiResponse.toResponseEntity(PostSuccessCode.POST_LIST_FOUND, service.findAll(cursor, size, category));
    }

    // 게시글 검색
    @Override
    @GetMapping(path = "/search")
    public ResponseEntity<ApiResponse<List<Post>>> search(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "memberId", required = false) Long memberId,
            @RequestParam(name = "category", required = false) Category category
    ) {
        return ApiResponse.toResponseEntity(PostSuccessCode.POST_SEARCHED, service.search(keyword, memberId, category));
    }

    // 게시글 단건 조회
    @Override
    @GetMapping(path = "/{postId}")
    public ResponseEntity<ApiResponse<Post>> findById(@PathVariable(name = "postId") long id) {
        return ApiResponse.toResponseEntity(PostSuccessCode.POST_FOUND, service.getDetail(id));
    }

    // 게시글 수정
    @Override
    @PutMapping(path = "/{postId}")
    public ResponseEntity<ApiResponse<Post>> update(
            @PathVariable(name = "postId") long id,
            @RequestBody UpdatePostRequest request
    ) {
        Post post = service.update(id, request.title(), request.content(), request.category());
        return ApiResponse.toResponseEntity(PostSuccessCode.POST_UPDATED, post);
    }

    // 게시글 삭제
    @Override
    @DeleteMapping(path = "/{postId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable(name = "postId") long id) {
        service.delete(id);
        return ApiResponse.toResponseEntity(PostSuccessCode.POST_DELETED, null);
    }
}
