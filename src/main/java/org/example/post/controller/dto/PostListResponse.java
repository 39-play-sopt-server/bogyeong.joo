package org.example.post.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.post.domain.Post;

import java.util.List;

@Schema(description = "커서 기반 게시글 목록 응답")
public record PostListResponse(
        @Schema(description = "생성일 내림차순, 동일 생성일이면 ID 내림차순으로 정렬한 목록") List<Post> posts,
        @Schema(description = "다음 요청의 cursor에 그대로 전달할 값. 다음 목록이 없으면 null입니다.", nullable = true) String nextCursor,
        @Schema(description = "다음 목록 존재 여부. false면 추가 요청을 중단합니다.", example = "false") boolean hasNext
) {
}
