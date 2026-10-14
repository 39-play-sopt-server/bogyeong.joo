package org.example.service;

import org.example.domain.Category;
import org.example.domain.Post;
import org.example.exception.PostNotFoundException;
import org.example.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
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

    public List<Post> findAll(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page는 0 이상이어야 합니다.");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size는 1 이상이어야 합니다.");
        }

        List<Post> posts = repository.findAll();
        // 큰 page와 size를 곱해도 int 범위를 넘어서 잘못된 인덱스가 되지 않도록 합니다.
        long offset = (long) page * size;
        if (offset >= posts.size()) {
            return List.of();
        }

        int fromIndex = (int) offset;
        int toIndex = (int) Math.min(offset + size, posts.size());
        return new ArrayList<>(posts.subList(fromIndex, toIndex));
    }

    public Post findById(long id) {
        Post post = repository.findById(id);
        if (post == null) {
            throw new PostNotFoundException(id);
        }
        return post;
    }

    // 상세 조회에서만 조회수를 증가시키고, 수정과 삭제에서는 기존 조회를 사용합니다.
    public Post getDetail(long id) {
        Post post = findById(id);
        post.increaseViewCount();
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
