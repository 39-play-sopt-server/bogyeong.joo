package org.example.member.service;

import org.example.global.apiPayload.exception.BusinessException;
import org.example.member.entity.Member;
import org.example.member.exception.MemberErrorCode;
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

    public Member findById(long id) {
        return repository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
    }

    public boolean existsById(long id) {
        return repository.existsById(id);
    }

    @Transactional
    public void withdraw(long authenticatedMemberId, long targetMemberId) {
        if (authenticatedMemberId != targetMemberId) {
            throw new BusinessException(MemberErrorCode.MEMBER_WITHDRAWAL_FORBIDDEN);
        }

        Member member = findById(targetMemberId);
        postRepository.deleteByMemberId(targetMemberId);
        repository.delete(member);
        repository.flush();
    }
}
