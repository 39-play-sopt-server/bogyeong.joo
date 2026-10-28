package org.example.post.exception;

import org.example.global.response.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PostErrorCode implements ErrorCode {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    TITLE_REQUIRED(HttpStatus.BAD_REQUEST, "제목은 비어 있을 수 없습니다."),
    CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "본문은 비어 있을 수 없습니다."),
    CATEGORY_REQUIRED(HttpStatus.BAD_REQUEST, "카테고리를 선택해주세요."),
    AUTHOR_REQUIRED(HttpStatus.BAD_REQUEST, "작성자 회원 ID를 입력해주세요."),
    INVALID_CURSOR(HttpStatus.BAD_REQUEST, "올바르지 않은 게시글 커서입니다."),
    INVALID_PAGE_SIZE(HttpStatus.BAD_REQUEST, "size는 1 이상 100 이하여야 합니다.");
    private final HttpStatus status;
    private final String message;

    PostErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return name(); }
    public String getMessage() { return message; }
}
