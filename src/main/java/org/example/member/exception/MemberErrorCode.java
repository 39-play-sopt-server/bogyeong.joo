package org.example.member.exception;

import org.example.global.response.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    NAME_REQUIRED(HttpStatus.BAD_REQUEST, "이름은 비어 있을 수 없습니다.");
    private final HttpStatus status;
    private final String message;

    MemberErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return name(); }
    public String getMessage() { return message; }
}
