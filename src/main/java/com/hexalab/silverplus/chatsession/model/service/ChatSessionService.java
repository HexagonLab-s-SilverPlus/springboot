package com.hexalab.silverplus.chatsession.model.service;

import com.hexalab.silverplus.chatsession.jpa.entity.ChatSessionEntity;
import com.hexalab.silverplus.chatsession.jpa.repository.ChatSessionRepository;
import com.hexalab.silverplus.chatsession.model.dto.ChatSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatSessionService {
    private final ChatSessionRepository chatSessionRepository;

    /**
     * 채팅 세션 생성 메소드
     * @param workspaceId
     * @param memUUID
     * @return
     */
    public ChatSession createSession(String workspaceId, String memUUID) {
        ChatSessionEntity sessionEntity = ChatSessionEntity.builder()
                .sessId(java.util.UUID.randomUUID().toString())
                .sessStartedAt(Timestamp.from(Instant.now()))
                .sessStatus("ACTIVE")
                .isEmergency("N") // Y/N (비상상황 여부)
                .sessTotMsgs(0)
                .sessType("T") // T/V (텍스트인지 보이스인지)
                .workspaceId(workspaceId)
                .sessMemUUID(memUUID)
                .build();

        return chatSessionRepository.save(sessionEntity).toDto();
    }




    public void updateSessionStatus(String workspaceId, String status) {
        // 세션 상태 유효성 검사
        if (!List.of("ACTIVE", "COMPLETED", "ERROR", "TIMEOUT").contains(status)) {
            throw new IllegalArgumentException("Invalid chat session status.");
        }

        // 세션 상태 업데이트(ACTIVE -> ?)
        ChatSessionEntity session = chatSessionRepository.findActiveSessionByWorkspaceId(workspaceId)
                .orElseThrow(() -> new RuntimeException("Session not found."));
        session.setSessStatus(status);

        // LAST_UPDATED 값 수정
        session.setLastUpdated(new Timestamp(System.currentTimeMillis()));

        chatSessionRepository.save(session);
    }


    /**
     * 채팅 세션 종료 메소드
     * @param sessId
     * @param status
     */
    public void endSession(String sessId, String status) {
        ChatSessionEntity session = chatSessionRepository.findById(sessId)
                .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));
        session.setSessStatus(status); // ACTIVE, COMPLETED, ERROR, TIMEOUT
        session.setSessEndedAt(Timestamp.from(Instant.now())); // 세션종료시간
        chatSessionRepository.save(session);
    }


    /**
     * 특정 워크스페이스의 활성화 상태인 세션 조회
     * @param workspaceId
     */
    @Transactional
    public void incrementSessionMessages(String workspaceId) {
        ChatSessionEntity session = chatSessionRepository.findActiveSessionByWorkspaceId(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("활성 세션을 찾을 수 없습니다."));

        // 총 메시지 수 증가
        session.setSessTotMsgs(session.getSessTotMsgs() + 1);

        // LAST_UPDATED 값 수정
        session.setLastUpdated(new Timestamp(System.currentTimeMillis()));

        // 세션 상태 업데이트 (? -> ACTIVE)
        session.setSessStatus("ACTIVE");
        chatSessionRepository.save(session);
    }



    /**
     * 활성화 상태인 세션 리스트 조회 메소드
     * @return
     */
    public List<ChatSession> getActiveSessions() {
        return chatSessionRepository.findBySessStatus("ACTIVE")
                .stream()
                .map(ChatSessionEntity::toDto)
                .toList();
    }
}