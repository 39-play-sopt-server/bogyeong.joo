package org.example.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(long id) {
        super("존재하지 않는 게시글입니다. (ID: " + id + ")");
    }
}
