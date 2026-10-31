package org.example.global.apiPayload.code;

import org.springframework.http.HttpStatus;

public interface SuccessCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
