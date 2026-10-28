package org.example.post.controller;

import org.example.post.controller.dto.CreatePostRequest;
import org.example.post.controller.dto.UpdatePostRequest;
import org.example.post.domain.Category;
import org.example.post.domain.Post;
import org.example.post.controller.docs.PostControllerDocs;
import org.example.global.response.ApiResponse;
import org.example.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.example.post.controller.dto.PostListResponse;

@RestController
@RequestMapping(path = "/api/v1/posts")
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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글이 작성되었습니다.", post));
    }

    // 게시글 목록 조회
    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<PostListResponse>> findAll(
            @RequestParam(name = "cursor", required = false) String cursor,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "category", required = false) Category category
    ) {
        return ResponseEntity.ok(ApiResponse.success("게시글 목록을 조회했습니다.", service.findAll(cursor, size, category)));
    }

    // 게시글 단건 조회
    @Override
    @GetMapping(path = "/{postId}")
    public ResponseEntity<ApiResponse<Post>> findById(@PathVariable(name = "postId") long id) {
        return ResponseEntity.ok(ApiResponse.success("게시글을 조회했습니다.", service.getDetail(id)));
    }

    // 게시글 수정
    @Override
    @PutMapping(path = "/{postId}")
    public ResponseEntity<ApiResponse<Post>> update(
            @PathVariable(name = "postId") long id,
            @RequestBody UpdatePostRequest request
    ) {
        Post post = service.update(id, request.title(), request.content(), request.category());
        return ResponseEntity.ok(ApiResponse.success("게시글이 수정되었습니다.", post));
    }

    // 게시글 삭제
    @Override
    @DeleteMapping(path = "/{postId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable(name = "postId") long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다.", null));
    }
}
