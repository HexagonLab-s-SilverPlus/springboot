package com.hexalab.silverplus.member.jpa.repository;

import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import com.hexalab.silverplus.member.model.dto.Member;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {

    long selectCheckId(String memId);
    MemberEntity findByMemId(String memId);
    long removeByMemId(String memId);
    List<MemberEntity> selectAllMember(Pageable pageable, Search search);
    long selectAllCount();
    long selectMemIdCount(String keyword);
    long selectMemNameCount(String keyword);
    long selectMemStatusCount(String keyword);
    long selectMemTypeCount(String keyword);
}
