package org.example.post.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.post.entity.Category;

@Schema(description = "게시글 수정 요청. 제목, 본문, 카테고리를 모두 전달합니다.")
public record UpdatePostRequest(
        @Schema(description = "수정할 제목. 공백만 입력할 수 없습니다.", example = "수정한 세미나 후기", requiredMode = Schema.RequiredMode.REQUIRED) String title,
        @Schema(description = "수정할 본문. 공백만 입력할 수 없습니다.", example = "트랜잭션도 학습했습니다.", requiredMode = Schema.RequiredMode.REQUIRED) String content,
        @Schema(description = "수정할 카테고리", example = "INFORMATION", requiredMode = Schema.RequiredMode.REQUIRED) Category category
) {
}
