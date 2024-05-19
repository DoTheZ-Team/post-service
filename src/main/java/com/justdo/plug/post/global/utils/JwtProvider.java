package com.justdo.plug.post.global.utils;

import com.justdo.plug.post.global.exception.ApiException;
import com.justdo.plug.post.global.response.code.status.ErrorStatus;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;

import static com.justdo.plug.post.global.utils.JwtProperties.*;

@Component
public class JwtProvider {

    private final SecretKey key;

    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        String keyEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.key = Keys.hmacShaKeyFor(keyEncoded.getBytes());
    }

    public Long getUserIdFromToken(HttpServletRequest request) {

        String accessToken = parseToken(request);

        return validateToken(accessToken);
    }

    public String parseToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        if (bearerToken == null || !bearerToken.startsWith(TOKEN_PREFIX)) {
            throw new ApiException(ErrorStatus._JWT_NOT_FOUND);
        } else {
            return bearerToken.substring(TOKEN_SPLIT);
        }
    }

    public Long validateToken(String accessToken) {
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(accessToken)
                    .getPayload().get(MEMBER_ID, Long.class);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new JwtException("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            throw new JwtException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            throw new JwtException("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            throw new JwtException("JWT 토큰이 잘못되었습니다.");
        }
    }
}
