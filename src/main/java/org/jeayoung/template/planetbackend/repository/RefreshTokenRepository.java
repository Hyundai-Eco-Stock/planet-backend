package org.jeayoung.template.planetbackend.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final StringRedisTemplate redis;

    private final Duration expiration = Duration.ofDays(30);

    // ── Public APIs ─────────────────────────────────────────────────────────────

    /**
     * 저장(기존 토큰 있으면 역매핑까지 정리 후 교체)
     */
    public void save(long memberId, String refreshToken) {

        final String memberKey = memberKey(memberId);

        // 기존 토큰 회수 후 새 토큰 저장 (파이프라인/트랜잭션 블록)
        redis.execute(new SessionCallback<Void>() {
            @Override
            public Void execute(RedisOperations operations) throws DataAccessException {

                operations.multi();
                String oldToken = (String) operations.opsForValue().get(memberKey);
                if (oldToken != null) {
                    operations.delete(tokenKey(oldToken));
                }
                operations.opsForValue().set(memberKey, refreshToken, expiration);
                operations.opsForValue()
                    .set(tokenKey(refreshToken), String.valueOf(memberId), expiration);
                operations.exec();
                return null;
            }
        });
    }

    /**
     * memberId로 토큰 조회
     */
    public String find(long memberId) {

        return redis.opsForValue().get(memberKey(memberId));
    }

    /**
     * refreshToken으로 memberId 조회 (없으면 null)
     */
    public Long findMemberIdByToken(String refreshToken) {

        String val = redis.opsForValue().get(tokenKey(refreshToken));
        return (val == null) ? null : Long.valueOf(val);
    }

    /**
     * memberId 기준 삭제 (역매핑 포함)
     */
    public void delete(long memberId) {

        final String mKey = memberKey(memberId);
        redis.execute(new SessionCallback<Void>() {
            @Override
            public Void execute(RedisOperations operations) throws DataAccessException {

                operations.multi();
                String token = (String) operations.opsForValue().get(mKey);
                if (token != null) {
                    operations.delete(tokenKey(token));
                }
                operations.delete(mKey);
                operations.exec();
                return null;
            }
        });
    }

    /**
     * refreshToken으로 삭제 (역매핑 포함)
     */
    public void deleteByToken(String refreshToken) {

        final String tKey = tokenKey(refreshToken);
        redis.execute(new SessionCallback<Void>() {
            @Override
            public Void execute(RedisOperations operations) throws DataAccessException {

                operations.multi();
                String memberId = (String) operations.opsForValue().get(tKey);
                if (memberId != null) {
                    operations.delete(memberKey(Long.parseLong(memberId)));
                }
                operations.delete(tKey);
                operations.exec();
                return null;
            }
        });
    }

    /**
     * memberId 기준 존재 여부
     */
    public boolean exists(long memberId) {

        Boolean has = redis.hasKey(memberKey(memberId));
        return has != null && has;
    }

    /**
     * refreshToken 기준 존재 여부 (O(1))
     */
    public boolean existsByToken(String refreshToken) {

        Boolean has = redis.hasKey(tokenKey(refreshToken));
        return has != null && has;
    }

    /**
     * 토큰 회전(rotate): 새 토큰으로 교체
     */
    public void rotate(long memberId, String newRefreshToken) {

        save(memberId, newRefreshToken);
    }

    // ── Key helpers ─────────────────────────────────────────────────────────────

    private String memberKey(long memberId) {

        return "REFRESH_TOKEN:" + memberId;
    }

    private String tokenKey(String refreshToken) {

        return "REFRESH_TOKEN_BY_TOKEN:" + refreshToken;
    }
}