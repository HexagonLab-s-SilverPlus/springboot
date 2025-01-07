package com.hexalab.silverplus.chat.model.service;

import com.hexalab.silverplus.chat.jpa.entity.ChatMessageEntity;
import com.hexalab.silverplus.chat.jpa.repository.ChatRepository;
import com.hexalab.silverplus.chat.model.dto.ChatMessage;
import com.hexalab.silverplus.workspace.model.dto.Workspace;
import com.hexalab.silverplus.workspace.model.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor    //매개변수 있는 생성자를 반드시 실행시켜야 한다는 설정임
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 설정
@DynamicUpdate // 변경된 필드만 업데이트하도록 설정
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 채팅 메시지 저장
     * @param chatMessage
     * @return
     */
    @Transactional
    public int saveChatMessage(ChatMessage chatMessage) {
        try {
            // 워크스페이스 존재 여부 확인
            if(chatMessage.getMsgWorkspaceId() == null || chatMessage.getMsgWorkspaceId().trim().isEmpty()){
                throw new IllegalArgumentException("워크스페이스 ID가 제공되지 않았습니다.");
            }

            // 전달받은 워크스페이스 ID로 실제 존재하는 워크스페이스인지 확인
            Workspace workspace = workspaceService.getWorkspaceByWorkspaceId(chatMessage.getMsgWorkspaceId())
                    .orElseThrow(() -> new IllegalArgumentException("워크스페이스 ID:  " + chatMessage.getMsgWorkspaceId()
                            + "에 해당하는 워크스페이스가 존재하지 않습니다."));

            // 워크스페이스
            //chatMessage.setMsgWorkspaceId(workspace.getWorkspaceId());

            // 메시지 저장
            ChatMessageEntity chatMessageEntity = chatMessage.toEntity();

            // userId 확인
            log.info("Saving ChatMessageEntity with msgId: {}", chatMessageEntity.getMsgId());

            if (chatMessageEntity.getMsgSenderUUID() == null || chatMessageEntity.getMsgSenderUUID().trim().isEmpty()) {
                throw new IllegalArgumentException("sender UUID가 빈 항목입니다.");
            }
            chatRepository.save(chatMessageEntity);
            return 1;
        } catch (DataIntegrityViolationException e) {
            log.error("Insert ChatMessage Data Integrity Violation: {}", e.getMessage(), e);
            return 0;
        } catch (Exception e) {
            log.error("Insert Chat Error: {}", e.getMessage(), e);
            e.printStackTrace();
            return 0;
        }
    }

    public List<ChatMessage> getChatHistory(String workspaceId) {
        List<ChatMessageEntity> chatHistory = chatRepository.findAllByMsgWorkspaceId(workspaceId);
        if(!chatHistory.isEmpty()){
            return chatHistory.stream()
                    .map(ChatMessageEntity::toDto) // toDto() 메소드 호출
                    .collect(Collectors.toList());
        }else{
            // 조회된 채팅 이력이 없다면 빈 리스트를 반환
            return new ArrayList<>();
        }
    }


}
