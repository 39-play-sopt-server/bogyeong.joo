package org.example.post.service;

import org.example.member.service.MemberService;

import org.example.post.domain.Category;
import org.example.post.domain.Post;
import org.example.post.exception.PostNotFoundException;
import org.example.post.exception.PostErrorCode;
import org.example.global.exception.BusinessException;
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

    public PostListResponse findAll(String cursor, int size, Category category, String title) {
        if (size < 1 || size > 100) {
            throw new BusinessException(PostErrorCode.INVALID_PAGE_SIZE);
        }

        PostCursor position = cursor == null ? null : PostCursor.decode(cursor);
        // 한 개를 더 조회해서 전체 개수 조회 없이 다음 목록의 존재 여부를 확인합니다.
        List<Post> fetched = repository.findNextPosts(category, toTitlePattern(title),
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

    private String toTitlePattern(String title) {
        if (title == null || title.isBlank()) {
            return null;
        }
        // 검색어의 %, _는 와일드카드가 아닌 실제 문자로 검색합니다.
        String escaped = title.trim().replace("!", "!!").replace("%", "!%").replace("_", "!_");
        return "%" + escaped + "%";
    }

    public Post findById(long id) {
        return repository.findById(id)
                .orElseThrow(PostNotFoundException::new);
    }

    // 상세 조회에서만 조회수를 증가시키고, 수정과 삭제에서는 기존 조회를 사용
    @Transactional
    public Post getDetail(long id) {
        Post post = findById(id);
        post.increaseViewCount();
        return post;
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
