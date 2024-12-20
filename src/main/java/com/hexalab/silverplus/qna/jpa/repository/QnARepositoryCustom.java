package com.hexalab.silverplus.qna.jpa.repository;

import com.querydsl.core.Tuple;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface QnARepositoryCustom {

    List<Map<String, Object>> selectMyQnA(String uuid, Pageable pageable);
}
