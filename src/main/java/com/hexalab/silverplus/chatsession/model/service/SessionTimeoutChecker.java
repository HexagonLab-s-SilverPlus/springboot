package com.hexalab.silverplus.chatsession.model.service;

import com.hexalab.silverplus.chatsession.jpa.entity.ChatSessionEntity;
import com.hexalab.silverplus.chatsession.jpa.repository.ChatSessionRepository;
import com.hexalab.silverplus.chatsession.model.dto.ChatSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class SessionTimeoutChecker {

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Transactional
    @Scheduled(fixedRate = 60000) // 1분 간격으로 해당 메소드가 호출됨
    public void checkSessionTimeouts() {
        // 현재 시간을 기준으로 10분전을 계산한다. 이 시간은 활동이 없었다고 간주하는 기준 시간이다.
        /* Threshold: 한계점(기준값, 문턱값), 컴퓨터 과학에서는 특정 동작을 트리거하는 기준으로 사용됨*/
        Timestamp timeoutThreshold = Timestamp.valueOf(LocalDateTime.now().minusMinutes(10)); // 10분 기준


        System.out.println("현재 시간: " + Timestamp.valueOf(LocalDateTime.now()));
        System.out.println("Threshold 기준 시간: " + timeoutThreshold);

        // 현재 활성화된(ACTIVE) 세션 중 마지막 업데이트 시간이 lastUpdated보다 이전인 세션을 가져온다.
        // 즉, 10분 동안 아무런 업데이트가 없었던 활성화된 세션을 찾는다.
        List<ChatSessionEntity> sessions = chatSessionRepository.findBySessStatusAndLastUpdatedBefore("ACTIVE", timeoutThreshold);
        System.out.println("Timeout 처리할 세션 개수: " + sessions.size());
        for (ChatSessionEntity session : sessions) {
            session.setSessStatus("TIMEOUT"); // 세션상태를 TIMEOUT으로 변경
            chatSessionRepository.save(session); // 변겨오딘 세션 객체를 데이터베이스에 저장
            System.out.println("세션 " + session.getSessId() + "가 TIMEOUT 상태로 변경되었습니다.");
            System.out.println("세션 마지막 업데이트 시간: " + session.getLastUpdated());
        }
    }
}