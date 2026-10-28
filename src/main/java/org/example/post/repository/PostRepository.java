package org.example.post.repository;

import org.example.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.example.post.domain.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @EntityGraph(attributePaths = "member")
    @Query("""
            SELECT p FROM Post p
            WHERE (:category IS NULL OR p.category = :category)
              AND (:titlePattern IS NULL OR p.title LIKE :titlePattern ESCAPE '!')
              AND (:createdAt IS NULL OR p.createdAt < :createdAt
                   OR (p.createdAt = :createdAt AND p.id < :id))
            ORDER BY p.createdAt DESC, p.id DESC
            """)
    List<Post> findNextPosts(
            @Param("category") Category category,
            @Param("titlePattern") String titlePattern,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "member")
    Optional<Post> findById(Long id);

    void deleteByMemberId(Long memberId);
}
