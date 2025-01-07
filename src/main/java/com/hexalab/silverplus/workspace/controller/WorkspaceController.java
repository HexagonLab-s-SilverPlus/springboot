package com.hexalab.silverplus.workspace.controller;

import com.hexalab.silverplus.workspace.model.dto.Workspace;
import com.hexalab.silverplus.workspace.model.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hexalab.silverplus.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/workspace")
@RequiredArgsConstructor
public class WorkspaceController {
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 특정 상태의 워크스페이스 조회 (페이징)
     *
     * @param memUuid         사용자 UUID
     * @param workspaceStatus 워크스페이스 상태 (ACTIVE, ARCHIVED, DELETED)
     * @param page            페이지 번호 (1-based)
     * @param size            페이지 크기
     * @return 페이징된 워크스페이스 목록
     */
    @GetMapping("/{memUuid}/status")
    public ResponseEntity<ApiResponse<List<Workspace>>> getWorkspacesByStatus(
            @PathVariable String memUuid,
            @RequestParam String workspaceStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            log.info("memUuid: {}", memUuid);
            log.info("workspaceStatus: {}", workspaceStatus);
            log.info("page: {}", page);
            log.info("size: {}", size);

            // 1-based page -> 0-based offset 계산
            int offset = (page - 1) * size;

            // 서비스 호출
            List<Workspace> workspaces = workspaceService.getWorkspacesPagedWithStatus(memUuid, offset, size, workspaceStatus);
            log.info("조회된 워크스페이스:{}", workspaces);

            // 결과가 없는 경우
            if (workspaces.isEmpty()) {
                // 데이터가 없더라도 200 상태와 빈 배열 반환
                return ResponseEntity.ok(ApiResponse.<List<Workspace>>builder()
                        .success(true)
                        .message(workspaceStatus + " 상태의 워크스페이스가 없습니다.")
                        .data(Collections.emptyList())
                        .build());
            }

            // 성공 응답
            return ResponseEntity.ok(ApiResponse.<List<Workspace>>builder()
                    .success(true)
                    .message(workspaceStatus + "의 워크스페이스 조회 성공")
                    .data(workspaces)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(workspaceStatus, "의 워크스페이스 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<List<Workspace>>builder()
                    .success(false)
                    .message(workspaceStatus + "의 워크스페이스 조회 실패")
                    .build());
        }
    }


//    @GetMapping("/deleted/{memUuid}")
//    public ResponseEntity<ApiResponse<List<Workspace>>> getDeletedWorkspaces(@PathVariable String memUuid) {
//        try {
//            List<Workspace> workspaces = workspaceService.getDeletedWorkspaces(memUuid);
//            if (workspaces.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.<List<Workspace>>builder()
//                        .success(false)
//                        .message("삭제된 워크스페이스가 존재하지 않습니다.")
//                        .build());
//            }
//            return ResponseEntity.ok(ApiResponse.<List<Workspace>>builder()
//                    .success(true)
//                    .message("삭제된 워크스페이스 조회 성공")
//                    .data(workspaces)
//                    .build());
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("삭제된 워크스페이스 조회 실패:", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<List<Workspace>>builder()
//                    .success(false)
//                    .message("삭제된 워크스페이스 조회 실패")
//                    .build());
//        }
//    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Workspace>> createWorkspace(
            @RequestParam String memUuid,
            @RequestParam String workspaceName) {
        try {
            log.info("Received memUuid: {}", memUuid);
            Workspace workspace = workspaceService.createWorkspace(memUuid, workspaceName);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<Workspace>builder()
                    .success(true)
                    .message("워크스페이스 생성 성공")
                    .data(workspace)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Workspace creation failed", e); // 디버깅용 로그 추가
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Workspace>builder()
                    .success(false)
                    .message("워크스페이스 생성 실패")
                    .build());
        }
    }


    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<ApiResponse<String>> deleteWorkspace(@PathVariable String workspaceId) {
        try {
            workspaceService.deleteWorkspace(workspaceId);
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("워크스페이스 삭제 성공")
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("워크스페이스 삭제 실패: ", e); // 디버그용 로그 추가
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<String>builder()
                    .success(false)
                    .message("워크스페이스 삭제 실패")
                    .build());
        }
    }


    /**
     * 워크스페이스를 ARCHIVED 상태로 변경
     *
     * @param workspaceId 워크스페이스 ID
     * @return
     */
    @PatchMapping("/{workspaceId}/archived")
    public ResponseEntity<ApiResponse<String>> archiveWorkspace(@PathVariable String workspaceId) {
        try {
            workspaceService.setWorkspaceAsFavorite(workspaceId);
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("워크스페이스가 ARCHIVED 상태로 설정되었습니다.")
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("워크스페이스 즐겨찾기 설정 실패:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<String>builder()
                    .success(false)
                    .message("워크스페이스 즐겨찾기 설정에 실패하였습니다.")
                    .build());
        }
    }


    /**
     * 워크스페이스를 ACTIVE 상태로 변경 (즐겨찾기 해제)
     *
     * @param workspaceId 워크스페이스 ID
     * @return ResponseEntity<ApiResponse < String>>
     */
    @PatchMapping("/{workspaceId}/active")
    public ResponseEntity<ApiResponse<String>> setWorkspaceAsActive(@PathVariable String workspaceId) {
        try {
            workspaceService.setWorkspaceAsActive(workspaceId); // 서비스 메서드 호출
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("워크스페이스가 ACTIVE 상태로 변경되었습니다.")
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("워크스페이스 ACTIVE 상태 변경 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<String>builder()
                    .success(false)
                    .message("워크스페이스 ACTIVE 상태 변경 실패")
                    .build());
        }
    }

}
