package org.example.auth.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 요청")
public record LoginRequest(
        @Schema(description = "가입한 이메일", example = "chulsu@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @Schema(description = "가입 시 사용한 비밀번호", example = "sopt1234!", format = "password", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {}
