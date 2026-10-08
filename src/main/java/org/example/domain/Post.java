package org.example.domain;

import java.time.LocalDateTime;

public class Post {
    private final long id;
    private String title;
    private String content;
    private Category category;
    private final String author;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Post(long id, String title, String content, Category category, String author) {
        validateText(title, "제목");
        validateText(content, "본문");
        validateText(author, "작성자");
        validateCategory(category);
        this.id = id;
        this.title = title.trim();
        this.content = content.trim();
        this.category = category;
        this.author = author.trim();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null;
    }

    // 모든 값을 먼저 검증해서 실패한 수정이 일부 필드만 바꾸지 않도록 합니다.
    public void update(String title, String content, Category category) {
        validateText(title, "제목");
        validateText(content, "본문");
        validateCategory(category);

        this.title = title.trim();
        this.content = content.trim();
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    private void validateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("카테고리를 선택해주세요.");
        }
    }

    private void validateText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "은 비어 있을 수 없습니다.");
        }
    }

    public long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public Category getCategory() {
        return category;
    }
    public String getAuthor() {
        return author;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
