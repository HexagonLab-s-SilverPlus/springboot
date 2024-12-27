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
     * 삭제되지 않은 워크스페이스 조회 (기본 조회로 설정)
     * @param memUuid
     * @return
     */
    @GetMapping("/{memUuid}")
    public ResponseEntity<ApiResponse<List<Workspace>>> getWorkspacesNotDeleted(@PathVariable String memUuid) {
        try {
            List<Workspace> workspaces = workspaceService.getWorkspacesNotDeleted(memUuid);
            if (workspaces.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.<List<Workspace>>builder()
                        .success(false)
                        .message("워크스페이스가 존재하지 않습니다.")
                        .build());
            }
            return ResponseEntity.ok(ApiResponse.<List<Workspace>>builder()
                    .success(true)
                    .message("워크스페이스 조회 성공")
                    .data(workspaces)
                    .build());
        } catch (Exception e) {
            log.error("워크스페이스 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<List<Workspace>>builder()
                    .success(false)
                    .message("워크스페이스 조회 실패")
                    .build());
        }
    }


    @GetMapping("/deleted/{memUuid}")
    public ResponseEntity<ApiResponse<List<Workspace>>> getDeletedWorkspaces(@PathVariable String memUuid){
        try{
            List<Workspace> workspaces = workspaceService.getDeletedWorkspaces(memUuid);
            if(workspaces.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.<List<Workspace>>builder()
                        .success(false)
                        .message("삭제된 워크스페이스가 존재하지 않습니다.")
                        .build());
            }
            return ResponseEntity.ok(ApiResponse.<List<Workspace>>builder()
                    .success(true)
                    .message("삭제된 워크스페이스 조회 성공")
                    .data(workspaces)
                    .build());
        }catch(Exception e){
            e.printStackTrace();
            log.error("삭제된 워크스페이스 조회 실패:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<List<Workspace>>builder()
                    .success(false)
                    .message("삭제된 워크스페이스 조회 실패")
                    .build());
        }
    }

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
    public ResponseEntity<ApiResponse<String>> deleteWorkspace(@PathVariable String workspaceId){
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

}
