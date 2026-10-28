package org.example.member.service;

import org.example.member.entity.Member;
import org.example.member.exception.MemberNotFoundException;
import org.example.member.repository.MemberRepository;
import org.example.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository repository;
    private final PostRepository postRepository;

    public MemberService(MemberRepository repository, PostRepository postRepository) {
        this.repository = repository;
        this.postRepository = postRepository;
    }

    @Transactional
    public Member create(String name) {
        return repository.save(new Member(name));
    }

    public Member findById(long id) {
        return repository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
    }

    @Transactional
    public void delete(long id) {
        Member member = findById(id);
        postRepository.deleteByMemberId(id);
        repository.delete(member);
        repository.flush();
    }
}
