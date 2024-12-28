package com.hexalab.silverplus.notice.jpa.repository;

import com.hexalab.silverplus.notice.jpa.entity.NoticeFilesEntity;
import com.hexalab.silverplus.notice.model.dto.NoticeFiles;

import java.util.ArrayList;
import java.util.List;

public interface NoticeFilesRepositoryCustom {
    int checkNoticeFiles(String notId);
    List<NoticeFilesEntity> selectNoticeFiles(String notId);
}
