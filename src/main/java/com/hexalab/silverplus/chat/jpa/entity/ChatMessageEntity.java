package com.hexalab.silverplus.chat.jpa.entity;

import com.hexalab.silverplus.chat.model.dto.ChatMessage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data  // Lombok을 이용한 Getter, Setter, ToString, Equals, HashCode 자동 생성
@AllArgsConstructor  // 모든 필드를 인자로 받는 생성자 생성
@NoArgsConstructor  // 기본 생성자 생성
@Builder  // 빌더 패턴 지원
@Table(name = "CHAT_MESSAGE") //매핑할 테이블 이름 지정함, NOTICE 테이블을 자동으로 만들어 주기도 하는 어노테이션임
@Entity //JPA 엔터티로 등록
public class ChatMessageEntity {
    @Id
    @Column(name = "MSG_ID", nullable = false)
    private String msgId;  // 메시지 ID (PK)

    @Column(name="MSG_SENDER_ROLE", nullable = false)
    private String msgSenderRole; // "user" 또는 "assistant"

    @Column(name = "MESSAGE", nullable = false, length = 2000)
    private String msgContent; // 메시지 내용

    @Column(name="MSG_SENT_AT", nullable = false)
    private Timestamp msgSentAt; // 전송 시각

    @Column(name = "MSG_SENDER_UUID", nullable = false, length = 50)
    private String msgSenderUUID;  // 작성자 UUID (FK)


    @Column(name = "MSG_TYPE", nullable = false)
    private String msgType; // 'T' OR 'V'


    @Column(name="PARENT_MSG_ID")
    private String parentMsgId; // 부모 메시지 ID (사용자 메시지에 대한 응답인 경우)


    @Column(name="MSG_WORKSPACE_ID", nullable = false)
    private String msgWorkspaceId; // 해당 워크스페이스 ID


    // DTO 변환 메서드
    public ChatMessage toDto() {
        return ChatMessage.builder()
                .msgId(this.msgId)
                .msgSenderRole(this.msgSenderRole)
                .msgContent(this.msgContent)
                .msgSentAt(this.msgSentAt)
                .msgSenderUUID(this.msgSenderUUID)
                .msgType(this.msgType)
                .parentMsgId(this.parentMsgId)
                .msgWorkspaceId(this.msgWorkspaceId)
                .build();
    }
}
