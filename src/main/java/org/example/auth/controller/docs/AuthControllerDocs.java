package org.example.auth.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.auth.controller.dto.request.LoginRequest;
import org.example.auth.controller.dto.response.LoginResponse;
import org.example.auth.controller.dto.request.SignupRequest;
import org.example.global.apiPayload.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "이메일·비밀번호 기반 회원가입 및 JWT 로그인 API.")
@ApiResponse(
        responseCode = "500",
        description = "서버 오류: INTERNAL_SERVER_ERROR. 현재 중복 이메일과 로그인 실패도 이 응답으로 처리됩니다.",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class),
                examples = @ExampleObject(value = AuthExamples.INTERNAL_SERVER_ERROR)))
public interface AuthControllerDocs {

    // 회원가입
    @Operation(
            summary = "회원가입",
            description = """
            이름, 이메일, 비밀번호를 받아 회원을 등록합니다.<br>
            """)
    @ApiResponse(
            responseCode = "200",
            description = "회원가입 성공. 공통 응답 객체가 아닌 회원 ID 숫자를 반환합니다.",
            useReturnTypeSchema = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = AuthExamples.SIGNUP_SUCCESS)))
    @ApiResponse(
            responseCode = "400",
            description = """
            이름 누락·공백: NAME_REQUIRED<br>
            이름 길이 초과: NAME_TOO_LONG<br>
            JSON·필드 타입 오류: INVALID_REQUEST
            """,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class),
                    examples = {
                            @ExampleObject(name = "NAME_REQUIRED", value = AuthExamples.NAME_REQUIRED),
                            @ExampleObject(name = "NAME_TOO_LONG", value = AuthExamples.NAME_TOO_LONG),
                            @ExampleObject(name = "INVALID_REQUEST", value = AuthExamples.INVALID_REQUEST)
                    }))
    ResponseEntity<Long> signup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "가입할 회원의 이름·이메일·비밀번호를 전달합니다.", required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SignupRequest.class),
                            examples = @ExampleObject(value = AuthExamples.SIGNUP_REQUEST))) SignupRequest request);

    // 로그인
    @Operation(
            summary = "로그인 및 JWT 발급",
            description = """
            가입한 이메일과 비밀번호를 확인한 후 JWT accessToken을 발급합니다.<br>
            이후 인증이 필요한 API에는 Authorization: Bearer {accessToken} 헤더를 전달합니다.<br>
            """)
    @ApiResponse(
            responseCode = "200",
            description = "로그인 성공. 공통 응답 객체가 아닌 accessToken 필드를 가진 객체를 반환합니다.",
            useReturnTypeSchema = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = AuthExamples.LOGIN_SUCCESS)))
    @ApiResponse(
            responseCode = "400",
            description = "JSON·필드 타입 오류: INVALID_REQUEST",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class),
                    examples = @ExampleObject(value = AuthExamples.INVALID_REQUEST)))
    ResponseEntity<LoginResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "가입한 이메일·비밀번호를 전달합니다.", required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(value = AuthExamples.LOGIN_REQUEST))) LoginRequest request);
}
