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
    boolean findByEmailName(String memEmail, String memName);
    boolean findByPhoneName(String memPhone, String memName);
    boolean findByEmailId(String memEmail, String memId);
    boolean findByPhoneId(String memCellphone, String memId);
    MemberEntity findByMemName(String memName);
    long updateMemPw(String memPw, String memUUID);
    MemberEntity findByKakaoProviderId(String kakaoProviderId);
    MemberEntity findByGoogleProviderId(String GoogleProviderId);
    MemberEntity findByNaverProviderId(String NaverProviderId);
}
