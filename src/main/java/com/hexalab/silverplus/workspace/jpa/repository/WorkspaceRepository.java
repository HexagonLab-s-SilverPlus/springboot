package com.hexalab.silverplus.workspace.jpa.repository;

import com.hexalab.silverplus.workspace.jpa.entity.WorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, String> {
    Optional<WorkspaceEntity> findByWorkspaceMemUuid(String memUuid);
    Optional<WorkspaceEntity> findWorkspaceByWorkspaceId(String workspaceId);
}
