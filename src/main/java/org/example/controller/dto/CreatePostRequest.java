package org.example.controller.dto;

import org.example.domain.Category;

public record CreatePostRequest(String title, String content, Category category, String author) {
}
