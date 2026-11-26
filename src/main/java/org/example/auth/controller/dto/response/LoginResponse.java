package org.example.auth.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 및 재발급 성공 데이터")
public record LoginResponse(
        @Schema(description = "JWT 접근 토큰. 기본 유효기간 30분. 인증 API 요청 시 Authorization: Bearer {accessToken} 헤더로 전달합니다. 예시 토큰은 실제 인증에 사용할 수 없습니다.",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIn0.example-signature", requiredMode = Schema.RequiredMode.REQUIRED)
        String accessToken
) {}
