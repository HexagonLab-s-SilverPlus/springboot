package com.hexalab.silverplus.qna.jpa.repository;

import com.hexalab.silverplus.common.Search;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface QnARepositoryCustom {
    Map<String, Object> selectADList(Pageable pageable, Search search);
    Map<String, Object> selectMyQnA(String uuid, Pageable pageable, Search search);

    int myCount(String uuid);
    int adTitleCount(String keyword);
    int adDateCount(Search search);
    int myTitleCount(String uuid, String keyword);
    int myDateCount(String uuid, Search search);
}
