package org.example.auth.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 요청")
public record SignupRequest(
        @Schema(description = "회원 이름. 앞뒤 공백 제거 후 1~50자", example = "철수", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "가입할 이메일. 기존 회원과 중복 불가", example = "chulsu@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @Schema(description = "가입할 비밀번호. 서버에서 BCrypt로 해싱해 저장", example = "sopt1234!", format = "password", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {}
