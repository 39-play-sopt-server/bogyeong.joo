package org.example.repository;

import org.example.domain.Post;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryPostRepository implements PostRepository {
    private final Map<Long, Post> posts = new HashMap<>();

    @Override
    public synchronized void save(Post post) {
        posts.put(post.getId(), post);
    }

    @Override
    public synchronized Post findById(long id) {
        return posts.get(id);
    }

    @Override
    public synchronized List<Post> findAll() {
        List<Post> result = new ArrayList<>(posts.values());
        result.sort(Comparator.comparingLong(Post::getId));
        return result;
    }

    @Override
    public synchronized void deleteById(long id) {
        posts.remove(id);
    }
}
