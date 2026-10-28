package org.example.post.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Base64;

record PostCursor(LocalDateTime createdAt, long id) {
    String encode() {
        String value = createdAt + "|" + id;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    static PostCursor decode(String cursor) {
        try {
            String value = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            String[] parts = value.split("\\|", -1);
            if (parts.length != 2) {
                throw new IllegalArgumentException();
            }
            long id = Long.parseLong(parts[1]);
            if (id <= 0) {
                throw new IllegalArgumentException();
            }
            return new PostCursor(LocalDateTime.parse(parts[0]), id);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new IllegalArgumentException("올바르지 않은 게시글 커서입니다.");
        }
    }
}
