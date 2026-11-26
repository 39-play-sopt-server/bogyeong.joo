package org.example.auth.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.auth.controller.dto.request.LoginRequest;
import org.example.auth.controller.dto.response.LoginResponse;
import org.example.auth.controller.dto.response.SignupResponse;
import org.example.auth.controller.dto.request.SignupRequest;
import org.example.global.apiPayload.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "이메일·비밀번호 기반 회원가입.")
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
            responseCode = "201",
            description = "회원가입 성공: MEMBER_CREATED. 공통 응답의 data.id에 생성된 회원 ID를 반환합니다.",
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
    ResponseEntity<BaseResponse<SignupResponse>> signup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "가입할 회원의 이름·이메일·비밀번호를 전달합니다.", required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SignupRequest.class),
                            examples = @ExampleObject(value = AuthExamples.SIGNUP_REQUEST))) SignupRequest request);

    // 로그인
    @Operation(
            summary = "로그인 및 JWT 발급",
            description = """
            가입한 이메일과 비밀번호를 확인한 후 Access Token과 Refresh Token을 발급합니다.<br>
            """)
    @ApiResponse(
            responseCode = "200",
            description = "로그인 성공: LOGIN_SUCCESS. Access Token은 헤더와 data.accessToken에 반환합니다.",
            headers = {
                    @Header(name = "Authorization", description = "Bearer {accessToken}"),
                    @Header(name = "Set-Cookie", description = "refreshToken 쿠키. 7일, HttpOnly, Secure, SameSite=Strict, Path=/auth/reissue")
            },
            useReturnTypeSchema = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = AuthExamples.LOGIN_SUCCESS)))
    @ApiResponse(
            responseCode = "400",
            description = "JSON·필드 타입 오류: INVALID_REQUEST",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class),
                    examples = @ExampleObject(value = AuthExamples.INVALID_REQUEST)))
    ResponseEntity<BaseResponse<LoginResponse>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "가입한 이메일·비밀번호를 전달합니다.", required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(value = AuthExamples.LOGIN_REQUEST))) LoginRequest request);

    @Operation(
            summary = "Access Token 재발급",
            description = """
            refreshToken 쿠키를 검증해 새 Access Token을 발급합니다.<br>
            """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{}"))))
    @ApiResponse(
            responseCode = "200",
            description = "재발급 성공: REISSUE_SUCCESS. 새 Access Token을 헤더와 data.accessToken에 반환합니다.",
            headers = @Header(name = "Authorization", description = "Bearer {accessToken}"),
            useReturnTypeSchema = true)
    @ApiResponse(
            responseCode = "401",
            description = "Refresh Token 누락 또는 검증 실패: INVALID_REFRESH_TOKEN",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class),
                    examples = @ExampleObject(value = AuthExamples.INVALID_REFRESH_TOKEN)))
    @ApiResponse(responseCode = "415", description = "지원하지 않는 Content-Type: UNSUPPORTED_MEDIA_TYPE")
    ResponseEntity<BaseResponse<LoginResponse>> reissue(
            @Parameter(name = "refreshToken", in = ParameterIn.COOKIE, required = true,
                    description = "로그인 성공 시 HTTPS 응답에서 받은 Refresh Token") String refreshToken);

    @Operation(
            summary = "로그아웃",
            description = """
            Access Token으로 인증하고 응답의 Set-Cookie로 refreshToken 쿠키를 만료합니다.
            """,
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "로그아웃 성공. 공통 응답의 code는 LOGOUT_SUCCESS입니다.",
            headers = @Header(name = "Set-Cookie", description = "refreshToken 쿠키 만료. Max-Age=0, Path=/auth/reissue"),
            useReturnTypeSchema = true)
    @ApiResponse(responseCode = "401", description = "Access Token이 없거나 유효하지 않음")
    ResponseEntity<BaseResponse<Void>> logout();
}
