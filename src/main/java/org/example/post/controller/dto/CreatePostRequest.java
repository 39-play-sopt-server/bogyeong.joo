package org.example.post.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.post.domain.Category;

@Schema(description = "게시글 작성 요청")
public record CreatePostRequest(
        @Schema(description = "제목. 공백만 입력할 수 없으며 앞뒤 공백은 제거됩니다.", example = "세미나 후기", requiredMode = Schema.RequiredMode.REQUIRED) String title,
        @Schema(description = "본문. 공백만 입력할 수 없으며 앞뒤 공백은 제거됩니다.", example = "JPA를 학습했습니다.", requiredMode = Schema.RequiredMode.REQUIRED) String content,
        @Schema(description = "카테고리", example = "DAILY", requiredMode = Schema.RequiredMode.REQUIRED) Category category,
        @Schema(description = "이미 생성된 작성자의 회원 ID. 이름이 같아도 ID로 구분합니다.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED) Long memberId
) {
}
