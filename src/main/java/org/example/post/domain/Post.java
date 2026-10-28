package org.example.post.domain;

import org.example.member.domain.Member;
import org.example.global.domain.BaseEntity;

import jakarta.persistence.Entity;
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
@Table(indexes = {
        @Index(name = "idx_post_created_at_id", columnList = "createdAt, id"),
        @Index(name = "idx_post_category_created_at_id", columnList = "category, createdAt, id")
})
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    private String content;
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    private LocalDateTime updatedAt;
    private long viewCount;

    protected Post() {
    }

    public Post(String title, String content, Category category, Member member) {
        validateText(title, "제목");
        validateText(content, "본문");
        if (member == null) {
            throw new IllegalArgumentException("작성자를 선택해주세요.");
        }
        validateCategory(category);
        this.title = title.trim();
        this.content = content.trim();
        this.category = category;
        this.member = member;
        this.updatedAt = null;
        this.viewCount = 0;
    }

    public synchronized void increaseViewCount() {
        this.viewCount++;
    }

    // 모든 값을 먼저 검증해서 실패한 수정이 일부 필드만 바꾸지 않도록 합니다.
    public synchronized void update(String title, String content, Category category) {
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
    public synchronized String getTitle() {
        return title;
    }
    public synchronized String getContent() {
        return content;
    }
    public synchronized Category getCategory() {
        return category;
    }
    public String getAuthor() {
        return member.getName();
    }
    public String getName() {
        return member.getName();
    }
    @JsonIgnore
    public Member getMember() {
        return member;
    }
    public synchronized LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public synchronized long getViewCount() {
        return viewCount;
    }
}
