package org.example.post.response.code;

import org.example.global.response.code.SuccessCode;
import org.springframework.http.HttpStatus;

public enum PostSuccessCode implements SuccessCode {
    POST_CREATED(HttpStatus.CREATED, "게시글이 작성되었습니다."),
    POST_LIST_FOUND(HttpStatus.OK, "게시글 목록을 조회했습니다."),
    POST_SEARCHED(HttpStatus.OK, "게시글을 검색했습니다."),
    POST_FOUND(HttpStatus.OK, "게시글을 조회했습니다."),
    POST_UPDATED(HttpStatus.OK, "게시글이 수정되었습니다."),
    POST_DELETED(HttpStatus.OK, "게시글이 삭제되었습니다.");
    private final HttpStatus status;
    private final String message;

    PostSuccessCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return name(); }
    public String getMessage() { return message; }
}
