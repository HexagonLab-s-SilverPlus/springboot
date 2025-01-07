package com.hexalab.silverplus.chatsession.jpa.repository;

import com.hexalab.silverplus.chatsession.jpa.entity.ChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, String> {
    // 상태로 세션 조회
    List<ChatSessionEntity> findBySessStatus(String sessStatus);

    // 워크스페이스ID로 활성화 상태인 세션 조회
    Optional<ChatSessionEntity> findActiveSessionByWorkspaceId(String workspaceId);

    // 특정 세션 상태(ACTIVE)와 lastUpdated 시간 이전에 마지막으로 업데이트된 세션들만 조회
    List<ChatSessionEntity> findBySessStatusAndLastUpdatedBefore(String sessStatus, Timestamp lastUpdated);
}

