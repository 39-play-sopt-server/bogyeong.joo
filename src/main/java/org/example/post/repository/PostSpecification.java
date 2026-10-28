package org.example.post.repository;

import jakarta.persistence.criteria.Predicate;
import org.example.post.entity.enums.Category;
import org.example.post.entity.Post;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class PostSpecification {
    private PostSpecification() {
    }

    public static Specification<Post> search(String keyword, Long memberId, Category category) {
        return (root, query, builder) -> {
            List<Predicate> conditions = new ArrayList<>();
            if (keyword != null) {
                String escaped = keyword.trim().replace("!", "!!").replace("%", "!%").replace("_", "!_");
                conditions.add(builder.like(root.get("title"), "%" + escaped + "%", '!'));
            }
            if (memberId != null) {
                conditions.add(builder.equal(root.get("member").get("id"), memberId));
            }
            if (category != null) {
                conditions.add(builder.equal(root.get("category"), category));
            }
            return builder.and(conditions.toArray(Predicate[]::new));
        };
    }
}
