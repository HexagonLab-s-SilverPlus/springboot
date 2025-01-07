package com.hexalab.silverplus.chatsession.jpa.repository;

import com.hexalab.silverplus.chatsession.jpa.entity.ChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, String> {
    List<ChatSessionEntity> findBySessStatus(String sessStatus); // 상태로 세션 조회
}