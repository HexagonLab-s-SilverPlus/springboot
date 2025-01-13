package com.hexalab.silverplus.common.sms.jpa.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Repository
public class SMSRepository {        // Redis Repository
    // Redis 키에 사용할 접두사
    private final String PREFIX = "SMS:";

    // Redis 객체 생성
    private final StringRedisTemplate stringRedisTemplate;

    // SMS 인증정보를 생성하는 메소드
    public void createSMSCertification(String memCellphone, String code) {
        int LIMIT_TIME = 3 * 60;
        stringRedisTemplate.opsForValue()
                .set(PREFIX + memCellphone, code, Duration.ofSeconds(LIMIT_TIME));
    }

    // SMS 인증정보를 가져오는 메소드
    public String getSMSCertification(String memCellphone) {
        return stringRedisTemplate.opsForValue().get(PREFIX + memCellphone);
    }

    // SMS 인증 정보를 삭제하는 메소드
    public void deleteSMSCertification(String memCellphone) {
        stringRedisTemplate.delete(PREFIX + memCellphone);
    }

    // 해당 키가 존재하는지 확인하는 메소드
    public boolean hasKey(String memCellphone) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(PREFIX + memCellphone));
    }
}
