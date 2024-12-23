package com.hexalab.silverplus.notice.jpa.repository;

import com.hexalab.silverplus.notice.jpa.entity.NoticeFilesEntity;
import com.hexalab.silverplus.notice.model.dto.NoticeFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NoticeFilesRepository extends JpaRepository<NoticeFilesEntity, UUID>,NoticeFilesRepositoryCustom {
}
