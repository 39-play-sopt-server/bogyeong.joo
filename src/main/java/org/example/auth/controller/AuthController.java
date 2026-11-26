package org.example.auth.controller;

import org.example.auth.controller.docs.AuthControllerDocs;
import org.example.auth.controller.dto.request.LoginRequest;
import org.example.auth.controller.dto.request.SignupRequest;
import org.example.auth.controller.dto.response.LoginResponse;
import org.example.auth.controller.dto.response.SignupResponse;
import org.example.auth.response.code.AuthSuccessCode;
import org.example.auth.service.AuthService;
import org.example.global.apiPayload.BaseResponse;
import org.example.member.response.code.MemberSuccessCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDocs {

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    private final AuthService authService;
    private final long refreshTokenExpiration;

    public AuthController(AuthService authService,
                          @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.authService = authService;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Override
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<SignupResponse>> signup(@RequestBody SignupRequest request) {
        Long memberId = authService.signup(request.name(), request.email(), request.password());
        return BaseResponse.toResponseEntity(MemberSuccessCode.MEMBER_CREATED, new SignupResponse(memberId));
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        AuthService.LoginTokens tokens = authService.login(request.email(), request.password());
        ResponseCookie refreshCookie = refreshCookie(tokens.refreshToken(), Duration.ofMillis(refreshTokenExpiration));

        return accessTokenResponse(AuthSuccessCode.LOGIN_SUCCESS, tokens.accessToken())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(BaseResponse.success(AuthSuccessCode.LOGIN_SUCCESS, new LoginResponse(tokens.accessToken())));
    }

    @Override
    @PostMapping(path = "/reissue", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<LoginResponse>> reissue(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
        String accessToken = authService.reissue(refreshToken);
        return accessTokenResponse(AuthSuccessCode.REISSUE_SUCCESS, accessToken)
                .body(BaseResponse.success(AuthSuccessCode.REISSUE_SUCCESS, new LoginResponse(accessToken)));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout() {
        return ResponseEntity.status(AuthSuccessCode.LOGOUT_SUCCESS.getStatus())
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.SET_COOKIE, refreshCookie("", Duration.ZERO).toString())
                .body(BaseResponse.success(AuthSuccessCode.LOGOUT_SUCCESS, null));
    }

    private ResponseCookie refreshCookie(String token, Duration maxAge) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth/reissue")
                .maxAge(maxAge)
                .build();
    }

    private ResponseEntity.BodyBuilder accessTokenResponse(AuthSuccessCode code, String accessToken) {
        return ResponseEntity.status(code.getStatus())
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    }
}

