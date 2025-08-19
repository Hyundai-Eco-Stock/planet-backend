package org.phoenix.planet.service.auth;

import lombok.RequiredArgsConstructor;
import org.phoenix.planet.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Transactional
    public void saveOrUpdate(long memberId, String refreshToken) {

        repository.save(memberId, refreshToken);
    }

    public String findByMemberId(long memberId) {

        return repository.find(memberId);
    }

    public long findMemberIdByRefreshToken(String refreshToken) {

        return repository.findMemberIdByToken(refreshToken);
    }

    public boolean validateExist(String refreshToken) {

        return repository.existsByToken(refreshToken);
    }

    public void delete(String refreshToken) {

        repository.deleteByToken(refreshToken);
    }
}
