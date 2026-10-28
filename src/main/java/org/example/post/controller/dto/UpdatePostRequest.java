package org.example.post.controller.dto;

import org.example.post.domain.Category;

public record UpdatePostRequest(String title, String content, Category category) {
}
