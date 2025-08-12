package org.jeayoung.template.planetbackend.service;

import lombok.RequiredArgsConstructor;
import org.jeayoung.template.planetbackend.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

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

}
