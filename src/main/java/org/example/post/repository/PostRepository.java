package org.example.post.repository;

import org.example.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.example.post.entity.enums.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @EntityGraph(attributePaths = "member")
    @Query("""
            SELECT p FROM Post p
            WHERE (:category IS NULL OR p.category = :category)
              AND (:createdAt IS NULL OR p.createdAt < :createdAt
                   OR (p.createdAt = :createdAt AND p.id < :id))
            ORDER BY p.createdAt DESC, p.id DESC
            """)
    List<Post> findNextPosts(
            @Param("category") Category category,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "member")
    Optional<Post> findById(Long id);

    @EntityGraph(attributePaths = "member")
    @Query("""
            SELECT p FROM Post p
            WHERE (:titlePattern IS NULL OR p.title LIKE :titlePattern ESCAPE '!')
              AND (:memberId IS NULL OR p.member.id = :memberId)
              AND (:category IS NULL OR p.category = :category)
            ORDER BY p.createdAt DESC, p.id DESC
            """)
    List<Post> search(
            @Param("titlePattern") String titlePattern,
            @Param("memberId") Long memberId,
            @Param("category") Category category);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    int increaseViewCount(@Param("id") long id);

    void deleteByMemberId(Long memberId);
}
