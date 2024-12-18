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
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WorkspaceService {
    @Autowired
    private WorkspaceRepository workspaceRepository;

    // 해당 UUID의 워크스페이스 추가하는 메소드
    public Workspace createWorkspace(String memUuid){
        try{
            WorkspaceEntity newWorkspaceEntity = WorkspaceEntity.builder()
                    .workspaceId(UUID.randomUUID().toString())
                    .workspaceName("New Workspace") // 기본 이름
                    .workspaceCreatedAt(Timestamp.from(Instant.now()))
                    .workspaceMemUuid(memUuid)
                    .workspaceStatus("ACTIVE")
                    .build();

            WorkspaceEntity savedWorkspaceEntity = workspaceRepository.save(newWorkspaceEntity);
            log.debug("Created Workspace: {}", savedWorkspaceEntity);
            return savedWorkspaceEntity.toDto(); // DTO로 반환Q
        }catch(Exception e){
            log.error("Failed to create workspace", e); // 디버깅용 로그 추가
            throw e; // 예외를 다시 던져서 상위 호출부에서 처리
        }
    }


    // 특정 UUID 멤버의 워크스페이스가 존재하는지 조회
    public Optional<Workspace> getWorkspaceByMemUuid(String memUuid){
        return workspaceRepository.findByWorkspaceMemUuid(memUuid)
                .map(WorkspaceEntity::toDto);
    }
}
