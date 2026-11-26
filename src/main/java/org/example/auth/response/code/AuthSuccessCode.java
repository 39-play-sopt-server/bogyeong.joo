package org.example.auth.response.code;

import org.example.global.apiPayload.code.SuccessCode;
import org.springframework.http.HttpStatus;

public enum AuthSuccessCode implements SuccessCode {
    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
    REISSUE_SUCCESS(HttpStatus.OK, "Access Token이 재발급되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃에 성공했습니다.");

    private final HttpStatus status;
    private final String message;

    AuthSuccessCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return name(); }
    public String getMessage() { return message; }
}
