package org.example.controller;

import org.example.controller.dto.CreatePostRequest;
import org.example.controller.dto.UpdatePostRequest;
import org.example.domain.Post;
import org.example.global.response.ApiResponse;
import org.example.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<Post>> create(@RequestBody CreatePostRequest request) {
        Post post = service.create(request.title(), request.content(), request.category(), request.author());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글이 작성되었습니다.", post));
    }

    // 게시글 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<Post>>> findAll() {

        return ResponseEntity.ok(ApiResponse.success("게시글 목록을 조회했습니다.", service.findAll()));
    }

    // 게시글 단건 조회
    @GetMapping(path = "/{postId}")
    public ResponseEntity<ApiResponse<Post>> findById(@PathVariable(name = "postId") long id) {
        return ResponseEntity.ok(ApiResponse.success("게시글을 조회했습니다.", service.getDetail(id)));
    }

    // 게시글 수정
    @PutMapping(path = "/{postId}")
    public ResponseEntity<ApiResponse<Post>> update(
            @PathVariable(name = "postId") long id,
            @RequestBody UpdatePostRequest request
    ) {
        Post post = service.update(id, request.title(), request.content(), request.category());
        return ResponseEntity.ok(ApiResponse.success("게시글이 수정되었습니다.", post));
    }

    // 게시글 삭제
    @DeleteMapping(path = "/{postId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable(name = "postId") long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다.", null));
    }
}
