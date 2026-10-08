package org.example.repository;

import org.example.domain.Post;
import java.util.List;

public interface PostRepository {
    void save(Post post);
    Post findById(long id);
    List<Post> findAll();
    void deleteById(long id);
}
