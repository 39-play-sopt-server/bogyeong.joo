package org.example.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.controller.dto.CreatePostRequest;
import org.example.controller.dto.UpdatePostRequest;
import org.example.domain.Post;
import org.example.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Post", description = "게시글 domain")
public interface PostControllerDocs {

    @Operation(
            summary = "게시글 작성",
            description = "제목, 본문, 카테고리와 작성자를 받아 게시글을 작성합니다.")
    ResponseEntity<ApiResponse<Post>> create(CreatePostRequest request);

    @Operation(
            summary = "게시글 목록 조회",
            description = "전체 게시글을 오름차순으로 조회합니다.")
    ResponseEntity<ApiResponse<List<Post>>> findAll();

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
