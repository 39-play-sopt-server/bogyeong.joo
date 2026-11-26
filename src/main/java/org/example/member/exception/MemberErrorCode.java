package org.example.member.exception;

import org.example.global.apiPayload.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    MEMBER_WITHDRAWAL_FORBIDDEN(HttpStatus.FORBIDDEN, "본인 계정만 탈퇴할 수 있습니다."),
    NAME_REQUIRED(HttpStatus.BAD_REQUEST, "이름은 비어 있을 수 없습니다."),
    NAME_TOO_LONG(HttpStatus.BAD_REQUEST, "이름은 50자 이하여야 합니다.");
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
