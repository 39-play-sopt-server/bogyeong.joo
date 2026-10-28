package org.example.post.service;

import org.example.member.service.MemberService;

import org.example.post.domain.Category;
import org.example.post.domain.Post;
import org.example.post.exception.PostNotFoundException;
import org.example.post.repository.PostRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
            throw new IllegalArgumentException("작성자 회원 ID를 입력해주세요.");
        }
        Post post = new Post(title, content, category, memberService.findById(memberId));
        return repository.save(post);
    }

    public List<Post> findAll(int page, int size, Category category) {
        if (page < 0) {
            throw new IllegalArgumentException("page는 0 이상이어야 합니다.");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size는 1 이상이어야 합니다.");
        }

        List<Post> posts = repository.findAll(Sort.by("id").ascending());
        if (category != null) {
            List<Post> filteredPosts = new ArrayList<>();
            for (Post post : posts) {
                if (post.getCategory() == category) {
                    filteredPosts.add(post);
                }
            }
            posts = filteredPosts;
        }
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
        return repository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
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
