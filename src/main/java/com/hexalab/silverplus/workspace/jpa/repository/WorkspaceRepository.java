package com.hexalab.silverplus.workspace.jpa.repository;

import com.hexalab.silverplus.workspace.jpa.entity.WorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, String> {
    // Optional은 다중 결과를 처리할 수 없다.
    // 따라서 워크스페이스 리스트를 조회할 때 Optional로 조회하려고 하면 다중 결과를 처리할 수 없다는 에러 로그가 뜸
    // Optional<WorkspaceEntity> findByWorkspaceMemUuid(String memUuid);
     List<WorkspaceEntity> findByWorkspaceMemUuid(String memUuid);
    Optional<WorkspaceEntity> findWorkspaceByWorkspaceId(String workspaceId);
}
