package org.example.post.dto.response;

import org.example.post.entity.Post;
import org.example.post.entity.enums.Category;

import java.time.LocalDateTime;

public record PostResponse(
        long id,
        String title,
        String content,
        Category category,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        long viewCount
) {
    public static PostResponse from(Post post) {
        return new PostResponse(post.getId(), post.getTitle(), post.getContent(), post.getCategory(),
                post.getMember().getName(), post.getCreatedAt(), post.getUpdatedAt(), post.getViewCount());
    }
}
