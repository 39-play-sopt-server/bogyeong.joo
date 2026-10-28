package org.example.post.controller.dto;

import org.example.post.domain.Category;

public record CreatePostRequest(String title, String content, Category category, Long memberId) {
}
