package com.hexalab.silverplus.chatsession.jpa.entity;

import com.hexalab.silverplus.chatsession.model.dto.ChatSession;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "CHAT_SESSION")
public class ChatSessionEntity {
    @Id
    @Column(name = "SESS_ID", nullable = false, length = 100)
    private String sessId;              // 세션 고유 ID

    @Column(name = "SESS_STARTED_AT", nullable = false)
    private Timestamp sessStartedAt;    // 세션 시작 시간

    @Column(name = "SESS_ENDED_AT")
    private Timestamp sessEndedAt;      // 세션 종료 시간

    @Column(name = "IS_EMERGENCY", nullable = false, length = 1)
    private String isEmergency;         // 긴급 여부 (Y/N)

    @Column(name = "SESS_STATUS", nullable = false, length = 20)
    private String sessStatus;          // 세션 상태 (ACTIVE, COMPLETED, ERROR, TIMEOUT)

    @Column(name = "SESS_TOT_MSGS")
    private int sessTotMsgs;            // 메시지 총 개수

    @Column(name = "SESS_TYPE", nullable = false, length = 10)
    private String sessType;            // 세션 타입 (TEXT/VOICE)

    @Column(name = "WORKSPACE_ID", nullable = false, length = 100)
    private String workspaceId;         // 워크스페이스 고유 ID

    @Column(name = "SESS_MEM_UUID", nullable = false, length = 100)
    private String sessMemUUID;         // 세션 주인 UUID

    public ChatSession toDto() {
        return ChatSession.builder()
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
