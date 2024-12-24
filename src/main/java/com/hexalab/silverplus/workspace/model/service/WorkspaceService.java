package com.hexalab.silverplus.workspace.model.service;

import com.hexalab.silverplus.workspace.jpa.entity.WorkspaceEntity;
import com.hexalab.silverplus.workspace.jpa.repository.WorkspaceRepository;
import com.hexalab.silverplus.workspace.model.dto.Workspace;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WorkspaceService {
    @Autowired
    private WorkspaceRepository workspaceRepository;

    /**
     * 새로운 워크스페이스 생성
     */
    public Workspace createWorkspace(String memUuid, String workspaceName) {
        try {
            WorkspaceEntity newWorkspaceEntity = WorkspaceEntity.builder()
                    .workspaceId(UUID.randomUUID().toString())
                    .workspaceName(workspaceName)
                    .workspaceCreatedAt(Timestamp.from(Instant.now()))
                    .workspaceMemUuid(memUuid)
                    .workspaceStatus("ACTIVE")
                    .build();

            WorkspaceEntity savedWorkspaceEntity = workspaceRepository.save(newWorkspaceEntity);
            log.debug("Created Workspace: {}", savedWorkspaceEntity);
            return savedWorkspaceEntity.toDto(); // DTO로 반환Q
        } catch (Exception e) {
            log.error("Failed to create workspace", e); // 디버깅용 로그 추가
            throw e; // 예외를 다시 던져서 상위 호출부에서 처리
        }
    }


    /**
     * 특정 사용자(UUID)의 워크스페이스 목록 조회
     */
    public List<Workspace> getWorkspaceByMemUuid(String memUuid) {
        return workspaceRepository.findByWorkspaceMemUuid(memUuid)
                .stream()
                .map(WorkspaceEntity::toDto) // Entity -> DTO 변환
                .collect(Collectors.toList());
    }

    /**
     * 워크스페이스 ID로 워크스펭시ㅡ 조회
     */
    public Optional<Workspace> getWorkspaceByWorkspaceId(String workspaceId) {
        return workspaceRepository.findWorkspaceByWorkspaceId(workspaceId)
               .map(WorkspaceEntity::toDto);
    }
}
