package org.example.post.service;

import org.example.member.service.MemberService;

import org.example.post.entity.enums.Category;
import org.example.post.entity.Post;
import org.example.post.exception.PostNotFoundException;
import org.example.post.exception.PostErrorCode;
import org.example.global.apiPayload.exception.BusinessException;
import org.example.post.repository.PostRepository;
import org.springframework.data.domain.PageRequest;
import org.example.post.controller.dto.PostListResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository repository;
    private final MemberService memberService;

    public PostService(PostRepository repository, MemberService memberService) {
        this.repository = repository;
        this.memberService = memberService;
    }

    @Transactional
    public Post create(String title, String content, Category category, Long memberId) {
        if (memberId == null) {
            throw new BusinessException(PostErrorCode.AUTHOR_REQUIRED);
        }
        Post post = new Post(title, content, category, memberService.findById(memberId));
        return repository.save(post);
    }

    public PostListResponse findAll(String cursor, int size, Category category) {
        if (size < 1 || size > 100) {
            throw new BusinessException(PostErrorCode.INVALID_PAGE_SIZE);
        }

        PostCursor position = cursor == null ? null : PostCursor.decode(cursor);
        List<Post> fetched = repository.findNextPosts(category,
                position == null ? null : position.createdAt(),
                position == null ? null : position.id(), PageRequest.of(0, size + 1));
        boolean hasNext = fetched.size() > size;
        List<Post> posts = List.copyOf(fetched.subList(0, Math.min(size, fetched.size())));
        String nextCursor = null;
        if (hasNext) {
            Post last = posts.getLast();
            nextCursor = new PostCursor(last.getCreatedAt(), last.getId()).encode();
        }
        return new PostListResponse(posts, nextCursor, hasNext);
    }

    public List<Post> search(String keyword, Long memberId, Category category) {
        if (keyword != null && keyword.isBlank()) {
            throw new BusinessException(PostErrorCode.SEARCH_KEYWORD_REQUIRED);
        }
        String titlePattern = null;
        if (keyword != null) {
            String escaped = keyword.trim().replace("!", "!!").replace("%", "!%").replace("_", "!_");
            titlePattern = "%" + escaped + "%";
        }
        return repository.search(titlePattern, memberId, category);
    }
    public Post findById(long id) {
        return repository.findById(id)
                .orElseThrow(PostNotFoundException::new);
    }

    @Transactional
    public Post getDetail(long id) {
        if (repository.increaseViewCount(id) == 0) {
            throw new PostNotFoundException();
        }
        return findById(id);
    }

    @Transactional
    public Post update(long id, String title, String content, Category category) {
        Post post = findById(id);
        post.update(title, content, category);
        return post;
    }

    @Transactional
    public void delete(long id) {
        findById(id);
        repository.deleteById(id);
    }
}
