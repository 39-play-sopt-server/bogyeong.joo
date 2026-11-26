package org.example.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    private static final String TOKEN_TYPE = "token_type";
    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String createAccessToken(Long memberId) {
        return createToken(memberId, ACCESS, accessTokenExpiration);
    }

    public String createRefreshToken(Long memberId) {
        return createToken(memberId, REFRESH, refreshTokenExpiration);
    }

    public Long getAccessTokenMemberId(String token) {
        Claims claims = parseClaims(token);
        Object tokenType = claims.get(TOKEN_TYPE);
        // 기존 Access Token에는 token_type이 없으므로 남은 유효기간 동안 허용한다.
        if (tokenType != null && !ACCESS.equals(tokenType)) {
            throw new JwtException("Access Token이 아닙니다.");
        }
        return Long.valueOf(claims.getSubject());
    }

    public Long getRefreshTokenMemberId(String token) {
        Claims claims = parseClaims(token);
        if (!REFRESH.equals(claims.get(TOKEN_TYPE))) {
            throw new JwtException("Refresh Token이 아닙니다.");
        }
        return Long.valueOf(claims.getSubject());
    }

    private String createToken(Long memberId, String tokenType, long expiration) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE, tokenType)
                .signWith(secretKey)
                .compact();
    }

    private Claims parseClaims(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        if (claims.getExpiration() == null) {
            throw new JwtException("토큰에 만료 시각이 없습니다.");
        }
        return claims;
    }
}
