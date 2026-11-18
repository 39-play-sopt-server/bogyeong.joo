package org.example.auth.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 성공 데이터")
public record SignupResponse(
        @Schema(description = "생성된 회원 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long id
) {}
