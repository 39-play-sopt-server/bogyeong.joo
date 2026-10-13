package org.example.controller.dto;

import org.example.domain.Category;

public record UpdatePostRequest(String title, String content, Category category) {
}
