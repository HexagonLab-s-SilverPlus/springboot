package com.hexalab.silverplus.chat.jpa.repository;

import com.hexalab.silverplus.chat.jpa.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessageEntity, String> {
    @Query("SELECT c FROM ChatMessageEntity c WHERE c.msgWorkspaceId = :msgWorkspaceId ORDER BY c.msgSentAt ASC")
    List<ChatMessageEntity> findAllByMsgWorkspaceId(@Param("msgWorkspaceId") String msgWorkspaceId);
}
