package com.hexalab.silverplus.security.jwt.model.service;

import com.hexalab.silverplus.security.jwt.jpa.entity.RefreshToken;
import com.hexalab.silverplus.security.jwt.jpa.repository.RefreshRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional      // import jakarta.transaction.Transactional;
@Service
public class RefreshService {
    private final RefreshRepository refreshRepository;

//    public RefreshService(RefreshRepository refreshRepository) {
//        this.refreshRepository = refreshRepository;
//    }

    public void save(RefreshToken refreshToken){
        refreshRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByTokenValue(String token){
        return refreshRepository.findByTokenValue(token);
    }

    public Boolean existsByTokenValue(String refreshValue){
        return refreshRepository.existsByTokenValue(refreshValue);
    }

    public void deleteByRefreshToken(String refreshValue){
        refreshRepository.deleteByTokenValue(refreshValue);
    }

    public List<RefreshToken> findByUserId(String userId){
        return refreshRepository.findByUserId(userId);
    }
}