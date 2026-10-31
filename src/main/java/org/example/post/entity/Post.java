package org.example.post.entity;

import org.example.member.entity.Member;
import org.example.post.entity.enums.Category;
import org.example.global.apiPayload.exception.BusinessException;
import org.example.post.exception.PostErrorCode;
import org.example.global.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import org.hibernate.annotations.DynamicUpdate;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@DynamicUpdate
@Table(indexes = {
        @Index(name = "idx_post_created_at_id", columnList = "createdAt, id"),
        @Index(name = "idx_post_category_created_at_id", columnList = "category, createdAt, id")
})
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(nullable = false, length = 10000)
    private String content;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32, columnDefinition = "varchar(32)")
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    private LocalDateTime updatedAt;
    private long viewCount;

    protected Post() {
    }

    public Post(String title, String content, Category category, Member member) {
        validateText(title, PostErrorCode.TITLE_REQUIRED, 100, PostErrorCode.TITLE_TOO_LONG);
        validateText(content, PostErrorCode.CONTENT_REQUIRED, 10000, PostErrorCode.CONTENT_TOO_LONG);
        if (member == null) {
            throw new BusinessException(PostErrorCode.AUTHOR_REQUIRED);
        }
        validateCategory(category);
        this.title = title.trim();
        this.content = content.trim();
        this.category = category;
        this.member = member;
        this.updatedAt = null;
        this.viewCount = 0;
    }

    // 모든 값을 먼저 검증해서 실패한 수정이 일부 필드만 바꾸지 않도록 합니다.
    public void update(String title, String content, Category category) {
        validateText(title, PostErrorCode.TITLE_REQUIRED, 100, PostErrorCode.TITLE_TOO_LONG);
        validateText(content, PostErrorCode.CONTENT_REQUIRED, 10000, PostErrorCode.CONTENT_TOO_LONG);
        validateCategory(category);

        this.title = title.trim();
        this.content = content.trim();
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    private void validateCategory(Category category) {
        if (category == null) {
            throw new BusinessException(PostErrorCode.CATEGORY_REQUIRED);
        }
    }

    private void validateText(String value, PostErrorCode errorCode, int maxLength, PostErrorCode lengthErrorCode) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(errorCode);
        }
        String trimmed = value.trim();
        if (trimmed.codePointCount(0, trimmed.length()) > maxLength) {
            throw new BusinessException(lengthErrorCode);
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
    public String getName() {
        return member.getName();
    }
    @JsonIgnore
    public Member getMember() {
        return member;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public long getViewCount() {
        return viewCount;
    }
}
