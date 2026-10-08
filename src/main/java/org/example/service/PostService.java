package org.example.service;

import org.example.domain.Category;
import org.example.domain.Post;
import org.example.exception.PostNotFoundException;
import org.example.repository.PostRepository;
import java.util.List;

public class PostService {
    private final PostRepository repository;
    private final PostIdGenerator idGenerator;

    public PostService(PostRepository repository, PostIdGenerator idGenerator) {
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    public Post create(String title, String content, Category category, String author) {
        Post post = new Post(idGenerator.generate(), title, content, category, author);
        repository.save(post);
        return post;
    }

    public List<Post> findAll() {
        return repository.findAll();
    }

    public Post findById(long id) {
        Post post = repository.findById(id);
        if (post == null) {
            throw new PostNotFoundException(id);
        }
        return post;
    }

    public Post update(long id, String title, String content, Category category) {
        Post post = findById(id);
        post.update(title, content, category);
        repository.save(post);
        return post;
    }

    public void delete(long id) {
        findById(id);
        repository.deleteById(id);
    }
}
