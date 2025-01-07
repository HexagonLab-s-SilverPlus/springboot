package com.hexalab.silverplus.chatsession.model.dto;

import com.hexalab.silverplus.chatsession.jpa.entity.ChatSessionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatSession {
    private String sessId;              // 세션 고유 ID
    private Timestamp sessStartedAt;    // 세션 시작 시간
    private Timestamp sessEndedAt;      // 세션 종료 시간
    private String isEmergency;         // 긴급 여부 (Y/N)
    private String sessStatus;          // 세션 상태 (ACTIVE, COMPLETED, ERROR, TIMEOUT)
    private int sessTotMsgs;            // 메시지 총 개수
    private String sessType;            // 세션 타입 (TEXT/VOICE)
    private String workspaceId;         // 워크스페이스 고유 ID
    private String sessMemUUID;         // 세션 주인 UUID

    public ChatSessionEntity toEntity() {
        return ChatSessionEntity.builder()
                .sessId(this.sessId)
                .sessStartedAt(this.sessStartedAt)
                .sessEndedAt(this.sessEndedAt)
                .isEmergency(this.isEmergency)
                .sessStatus(this.sessStatus)
                .sessTotMsgs(this.sessTotMsgs)
                .sessType(this.sessType)
                .workspaceId(this.workspaceId)
                .sessMemUUID(this.sessMemUUID)
                .build();
    }
}
