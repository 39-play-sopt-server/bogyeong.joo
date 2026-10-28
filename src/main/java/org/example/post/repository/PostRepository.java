package org.example.post.repository;

import org.example.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Override
    @EntityGraph(attributePaths = "member")
    List<Post> findAll(Sort sort);

    @Override
    @EntityGraph(attributePaths = "member")
    Optional<Post> findById(Long id);

    void deleteByMemberId(Long memberId);
}
