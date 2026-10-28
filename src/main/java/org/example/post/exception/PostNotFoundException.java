package org.example.post.exception;

import org.example.global.exception.BusinessException;

public class PostNotFoundException extends BusinessException {
    public PostNotFoundException() {
        super(PostErrorCode.POST_NOT_FOUND);
    }
}
