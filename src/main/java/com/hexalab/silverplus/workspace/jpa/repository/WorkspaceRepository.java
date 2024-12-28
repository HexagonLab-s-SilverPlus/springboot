package com.hexalab.silverplus.workspace.jpa.repository;

import com.hexalab.silverplus.workspace.jpa.entity.WorkspaceEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, String> {
    // Optional 은 다중 결과를 처리할 수 없다.
    // 따라서 워크스페이스 리스트를 조회할 때 Optional로 조회하려고 하면 다중 결과를 처리할 수 없다는 에러 로그가 뜸
    // Optional<WorkspaceEntity> findByWorkspaceMemUuid(String memUuid);

    // 전체 워크스페이스 조회
    List<WorkspaceEntity> findByWorkspaceMemUuid(String memUuid);

    Optional<WorkspaceEntity> findWorkspaceByWorkspaceId(String workspaceId);


    // 특정 status(ARCHIVED, ACTIVE, DELETED)인 워크스페이스를 페이징 처리해서 조회
    /*
        Spring Data JPA는 메소드 이름을 기반으로 쿼리를 생성하는데,
        paeged 라는 속성은 WorkspaceEntity에 존재하지 않으므로 쿼리 생성에 실패한다.
    */
//    List<WorkspaceEntity> findByWorkspaceMemUuidAndWorkspaceStatusPaged(String memUuid, String workspaceStatus, Pageable pageable);
    /*
    메소드 이름에서 Paged를 제거하고 JPA가 쿼리를 제대로 생성할 수 있도록 수정한다.
    Pageable은 JPA에서 페이징 처리를 위한 매개변수로 사용되며, 메소드 이름에 포함하지 않아도 됨
    그냥 매개변수에 Pageable 인자를 추가하면, JPA가 페이징 가능한 쿼리를 생성한다.
    * */
    List<WorkspaceEntity> findByWorkspaceMemUuidAndWorkspaceStatus(String memUuid, String workspaceStatus, Pageable pageable);

}
