package com.hexalab.silverplus.member.jpa.repository;

import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import com.hexalab.silverplus.member.model.dto.Member;
import com.querydsl.core.Tuple;
import org.hibernate.sql.model.ast.builder.TableUpdateBuilder;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
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
    MemberEntity findBySocialPi(String provider, String socialPi);
    long updateSocial(Boolean linking, String provider, String socialPi, String memUUID);
    MemberEntity findByProfile(String memSeniorProfile);
    List<MemberEntity> selectAllSenior(Pageable pageable, Search search, String memUUID, String type);
    long selectSeniorCount(String keyword, String memUUID, String action, String type);
    long updateApproval(String memUUID, String status);
    long selectNeedApprovalCount(String memUUID);
    Map<String, Object> selectAllSeniorFam(Pageable pageable, Search search);
    long selectAllSeniorFamCount();
    long selectSeniorNameFamCount(String keyword);
    long updateSeniorFamApproval(String memUUID, String relationship, String memUUIDFam);
    long selectAllSeniorCount(String memUUID);
    Map<String, Object> selectApprovalList(Pageable pageable, Search search, String memUUID);
    long selectApprovalCount(String memUUID);

}
