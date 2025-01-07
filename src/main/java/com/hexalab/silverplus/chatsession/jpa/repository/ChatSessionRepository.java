package com.hexalab.silverplus.chatsession.jpa.repository;

import com.hexalab.silverplus.chatsession.jpa.entity.ChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, String> {
    List<ChatSessionEntity> findBySessStatus(String sessStatus); // 상태로 세션 조회
    // 워크스페이스ID로 활성화 상태인 세션 조회
    Optional<ChatSessionEntity> findActiveSessionByWorkspaceId(String workspaceId);

}