package org.example.auth.service;

import io.jsonwebtoken.JwtException;
import org.example.auth.response.code.AuthErrorCode;
import org.example.global.apiPayload.exception.BusinessException;
import org.example.global.security.jwt.JwtProvider;
import org.example.member.entity.Member;
import org.example.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public Long signup(String name, String email, String password) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        Member member = new Member(name, email, passwordEncoder.encode(password));
        return memberRepository.save(member).getId();
    }

    @Transactional(readOnly = true)
    public LoginTokens login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        Long memberId = member.getId();
        return new LoginTokens(jwtProvider.createAccessToken(memberId), jwtProvider.createRefreshToken(memberId));
    }

    @Transactional(readOnly = true)
    public String reissue(String refreshToken) {
        Long memberId;
        try {
            memberId = jwtProvider.getRefreshTokenMemberId(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (!memberRepository.existsById(memberId)) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        return jwtProvider.createAccessToken(memberId);
    }

    public record LoginTokens(String accessToken, String refreshToken) {}
}
