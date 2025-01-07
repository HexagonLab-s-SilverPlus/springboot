package com.hexalab.silverplus.chatsession.controller;

import com.hexalab.silverplus.chatsession.model.dto.ChatSession;
import com.hexalab.silverplus.chatsession.model.service.ChatSessionService;
import com.hexalab.silverplus.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
public class ChatSessionController {
    private final ChatSessionService chatSessionService;

    // 세션 생성
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<ChatSession>> startSession(@RequestParam String workspaceId, @RequestParam String memUUID) {
        try {
            ChatSession session = chatSessionService.createSession(workspaceId, memUUID);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<ChatSession>builder()
                            .success(true)
                            .message("세션 생성 성공")
                            .data(session)
                            .build());
        } catch (Exception e) {
            log.error("세션 생성 오류:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChatSession>builder()
                            .success(false)
                            .message("세션 생성 실패")
                            .build());
        }
    }

    // 세션 종료
    @PatchMapping("/end")
    public ResponseEntity<ApiResponse<String>> endSession(@RequestParam String sessId, @RequestParam String status) {
        try {
            chatSessionService.endSession(sessId, status);
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("세션 종료 성공")
                    .build());
        } catch (Exception e) {
            log.error("세션 종료 오류:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .message("세션 종료 실패")
                            .build());
        }
    }

    // 활성 세션 조회
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ChatSession>>> getActiveSessions() {
        try {
            List<ChatSession> activeSessions = chatSessionService.getActiveSessions();
            return ResponseEntity.ok(ApiResponse.<List<ChatSession>>builder()
                    .success(true)
                    .message("활성 세션 조회 성공")
                    .data(activeSessions)
                    .build());
        } catch (Exception e) {
            log.error("활성 세션 조회 오류:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<ChatSession>>builder()
                            .success(false)
                            .message("활성 세션 조회 실패")
                            .build());
        }
    }
}