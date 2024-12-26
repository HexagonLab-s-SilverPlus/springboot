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
                .map(WorkspaceEntity::toDto)
                .toList();
    }

    /**
     * 워크스페이스 ID로 워크스페이스 조회
     */
    public Optional<Workspace> getWorkspaceByWorkspaceId(String workspaceId) {
        return workspaceRepository.findWorkspaceByWorkspaceId(workspaceId)
                .map(WorkspaceEntity::toDto);
    }


    /**
     * 워크스페이스 삭제 (상태를 DELETED로 변경)
     */
    public void deleteWorkspace(String workspaceId) {
        Optional<WorkspaceEntity> workspaceOptional = workspaceRepository.findWorkspaceByWorkspaceId(workspaceId);
        if (workspaceOptional.isPresent()) {
            WorkspaceEntity workspaceEntity = workspaceOptional.get();
            workspaceEntity.setWorkspaceStatus("DELETED"); // 상태를 DELETED 로 업데이트
            workspaceEntity.setWorkspaceDeletedAt(Timestamp.from(Instant.now())); // 삭제 시간 업데이트
            workspaceRepository.save(workspaceEntity);
            log.info("Workspace deleted (marked as DELETED): {}", workspaceId);
        } else {
            log.error("Workspace not found: {}", workspaceId);
            throw new RuntimeException("Workspace not found");
        }
    }

    /**
     * 삭제되지 않은 워크스페이스만 조회하는 메소드
     */
    public List<Workspace> getWorkspacesNotDeleted(String memUuid) {
        return workspaceRepository.findByWorkspaceMemUuidAndWorkspaceStatusNot(memUuid, "DELETED")
                .stream()
                .map(WorkspaceEntity::toDto)
                .toList();

    }


    /**
     * 삭제된 워크스페이스만 조회하는 메소드
     */
    public List<Workspace> getDeletedWorkspaces(String memUuid) {
        // 1. 회원 UUID에 해당하는 워크스페이스 중에서 workspaceStatus가 "ACTIVE"가 아닌 데이터만 가져온다.
        /* return: List<WorkspaceEntity> */
        return workspaceRepository.findByWorkspaceMemUuidAndWorkspaceStatusNot(memUuid, "ACTIVE")
                // 2. List<WorkspaceEntity>를 Java Stream으로 변환하여 처리한다.
                // 스트림을 사용하면 데이터를 필터링하거나 변환할 때 간결하게 처리할 수 있다.
                // Stream은 컬렉션에 저장된 요소를 하나씩 처리할 수 있는 연속된 데이터의 흐름.
                /* return: Stream<WorkspaceEntity> */
                .stream()
                // 3. stream으로 전달된 각 워크스페이스(workspace)를 필터링한다.
                // workspaceStatus가 "DELETED"인 워크스페이스만 통과.
                // 즉, "ACTIVE"가 아닌 워크스페이스 중에서 최종적으로 "DELETED" 상태만 선택된다.
                /* return: Stream<WorkspaceEntity>*/
                .filter(workspace -> "DELETED".equals(workspace.getWorkspaceStatus()))
                // 4. 필터링된 WorkspaceEntity 객체를 Workspace DTO로 변환한다.(WorkspaceEntity의 toDto 메소드 호출)
                /* return: Stream<Workspace> */
                .map(WorkspaceEntity::toDto)
                // 5. 스트림의 결과를 다시 List로 변환한다.
                // 최종적으로 "DELETED" 상태의 워크스페이스만 포함된 List<Workspace>를 반환한다.
                /* return: List<Workspace> */
                .toList();
    }
}
