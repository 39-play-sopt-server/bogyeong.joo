package org.example.post.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.post.controller.dto.CreatePostRequest;
import org.example.post.controller.dto.UpdatePostRequest;
import org.example.post.domain.Category;
import org.example.post.domain.Post;
import org.example.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import org.example.post.controller.dto.PostListResponse;

@Tag(name = "Post", description = "게시글 domain")
public interface PostControllerDocs {

    @Operation(
            summary = "게시글 작성",
            description = "제목, 본문, 카테고리와 작성자 회원 ID를 받아 게시글을 작성합니다.")
    ResponseEntity<ApiResponse<Post>> create(CreatePostRequest request);

    @Operation(
            summary = "게시글 목록 조회",
            description = "게시글을 생성일 기준 최신순으로 조회합니다.(+커서 페이지네이션 적용, +카테고리 검색 적용)")
    ResponseEntity<ApiResponse<PostListResponse>> findAll(
            @Parameter(name = "cursor", description = "이전 응답의 nextCursor. 첫 조회에서는 생략합니다.") String cursor,
            @Parameter(name = "size") int size,
            @Parameter(name = "category") Category category
    );

    @Operation(
            summary = "게시글 상세 조회",
            description = "게시글을 조회하고 조회수를 1 증가시킵니다.")
    ResponseEntity<ApiResponse<Post>> findById(long id);

    @Operation(
            summary = "게시글 수정",
            description = "게시글의 제목, 본문과 카테고리를 수정합니다.")
    ResponseEntity<ApiResponse<Post>> update(long id, UpdatePostRequest request);

    @Operation(
            summary = "게시글 삭제",
            description = "게시글을 삭제합니다.")
    ResponseEntity<ApiResponse<Void>> delete(long id);
}
