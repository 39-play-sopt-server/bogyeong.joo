package org.example.auth.controller;

import org.example.auth.controller.docs.AuthControllerDocs;
import org.example.auth.controller.dto.request.SignupRequest;
import org.example.auth.controller.dto.request.LoginRequest;
import org.example.auth.controller.dto.response.LoginResponse;
import org.example.auth.controller.dto.response.SignupResponse;
import org.example.auth.response.code.AuthSuccessCode;
import org.example.auth.service.AuthService;
import org.example.global.apiPayload.BaseResponse;
import org.example.member.response.code.MemberSuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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
        String accessToken = authService.login(request.email(), request.password());
        return BaseResponse.toResponseEntity(AuthSuccessCode.LOGIN_SUCCESS, new LoginResponse(accessToken));
    }
}

