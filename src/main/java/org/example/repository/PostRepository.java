package org.example.repository;

import org.example.domain.Post;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository {
    void save(Post post);
    Post findById(long id);
    List<Post> findAll();
    void deleteById(long id);
}
