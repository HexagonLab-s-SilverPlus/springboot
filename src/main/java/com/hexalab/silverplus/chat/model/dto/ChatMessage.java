package com.hexalab.silverplus.chat.model.dto;

import com.hexalab.silverplus.chat.jpa.entity.ChatMessageEntity;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String msgId; // UUID -> String
    @NotEmpty(message = "Sender Role is required")
    private String msgSenderRole;
    @NotEmpty(message = "Content is required")
    private String msgContent;
    private Timestamp msgSentAt;
    @NotEmpty(message = "Sender UUID is required")
    private String msgSenderUUID;
    private String msgType; // T(EXT) or V(OICE)
    private String parentMsgId; // 상위 메시지 ID
    private String msgWorkspaceId; // 해당 워크스페이스 ID

    public ChatMessageEntity toEntity(){
        return ChatMessageEntity.builder()
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
