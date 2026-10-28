package org.example.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 생성 요청")
public record CreateMemberRequest(
        @Schema(description = "회원 이름. 중복을 허용하며 공백만 입력할 수 없습니다. 앞뒤 공백은 제거됩니다.", example = "보경", requiredMode = Schema.RequiredMode.REQUIRED) String name
) {
}
