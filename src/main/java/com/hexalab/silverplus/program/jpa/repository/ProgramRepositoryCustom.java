package com.hexalab.silverplus.program.jpa.repository;

import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.program.jpa.entity.ProgramEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ProgramRepositoryCustom {

    int selectNearbyListCount(String keyword);
    int selectTitleListCount(String keyword);
    int selectContentListCount(String keyword);
    int selectAreaListCount(String keyword);
    int selectOrgNameListCount(String keyword);
    int selectDateListCount(Search search);

    Map<String, Object> selectSearchList(Pageable pageable, Search search);

    //Map<String, Object> selectNearbyPrograms(String memAddress, Pageable pageable, Search search);
}//ProgramRepositoryCustom end
