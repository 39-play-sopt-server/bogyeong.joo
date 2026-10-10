package org.example.repository;

import org.example.domain.Post;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryPostRepository implements PostRepository {
    private final Map<Long, Post> posts = new HashMap<>();

    @Override
    public void save(Post post) {
        posts.put(post.getId(), post);
    }

    @Override
    public Post findById(long id) {
        return posts.get(id);
    }

    @Override
    public List<Post> findAll() {
        List<Post> result = new ArrayList<>(posts.values());
        result.sort(Comparator.comparingLong(Post::getId));
        return result;
    }

    @Override
    public void deleteById(long id) {
        posts.remove(id);
    }
}
