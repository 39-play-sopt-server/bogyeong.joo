package org.example.member.response.code;

import org.example.global.apiPayload.code.SuccessCode;
import org.springframework.http.HttpStatus;

public enum MemberSuccessCode implements SuccessCode {
    MEMBER_CREATED(HttpStatus.CREATED, "회원이 생성되었습니다."),
    MEMBER_DELETED(HttpStatus.OK, "회원이 삭제되었습니다.");
    private final HttpStatus status;
    private final String message;

    MemberSuccessCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return name(); }
    public String getMessage() { return message; }
}
