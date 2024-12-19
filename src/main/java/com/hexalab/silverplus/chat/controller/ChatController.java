package com.hexalab.silverplus.chat.controller;

import com.hexalab.silverplus.chat.model.dto.ChatMessage;
import com.hexalab.silverplus.chat.model.service.ChatService;
import org.myweb.first.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // CORS 설정 (보안을 위해 필요한 대로 설정하기)
public class ChatController {
    @Autowired
    private ChatService chatService;

    // 챗 메시지 저장
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<ChatMessage>> saveChatMessage(@RequestBody ChatMessage chatMessage) {
        int result = chatService.saveChatMessage(chatMessage);
        if(result>0){
            // 성공 응답 생성
            ApiResponse<ChatMessage> response = ApiResponse.<ChatMessage>builder()
                    .success(true)
                    .message("채팅 메시지 저장 성공")
                    .data(chatMessage)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // 성공 응답 반환
        }else{
            // 실패 응답 생성
            ApiResponse<ChatMessage> response = ApiResponse.<ChatMessage>builder()
                    .success(false)
                    .message("채팅 메시지 저장 실패! 다시 시도해 주세요.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 실패 응답 반환
        }

    }

    // 챗 메세지 조회
    @GetMapping("/history/{workspaceId}")
    public ResponseEntity<ApiResponse<List<ChatMessage>>> getChatHistory(@PathVariable String workspaceId) {
        try {
            List<ChatMessage> history = chatService.getChatHistory(workspaceId);
            if (!history.isEmpty()) {
                ApiResponse<List<ChatMessage>> response = ApiResponse.<List<ChatMessage>>builder()
                        .success(true)
                        .message("내 채팅 메시지 조회 성공")
                        .data(history)
                        .build();

                return ResponseEntity.ok(response);
            } else {
                ApiResponse<List<ChatMessage>> response = ApiResponse.<List<ChatMessage>>builder()
                        .success(true)
                        .message("조회할 채팅 메시지 이력이 없습니다.")
                        .data(history)
                        .build();
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
            }

        } catch (Exception e) {
            ApiResponse<List<ChatMessage>> response = ApiResponse.<List<ChatMessage>>builder()
                    .success(false)
                    .message("채팅 메시지 조회 실패! 다시 시도해 주세요.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
