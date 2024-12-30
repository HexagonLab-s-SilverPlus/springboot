package com.hexalab.silverplus.notice.jpa.repository;

import com.hexalab.silverplus.notice.jpa.entity.NoticeEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoticeRepositoryCustom {
    long selectAllNoticeListCount();
    List<NoticeEntity> selectAllNoticeList(Pageable pageable);
    long selectSearchTitleNoticeListCount(String keyword);
    long selectSearchContentNoticeListCount(String keyword);
    List<NoticeEntity> selectSearchTitleNoticeList(String keyword, Pageable pageable);
    List<NoticeEntity> selectSearchContentNoticeList(String keyword, Pageable pageable);


}
