package com.hexalab.silverplus.member.model.service;

import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import com.hexalab.silverplus.member.jpa.repository.MemberRepository;
import com.hexalab.silverplus.member.model.dto.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional      // 트랜젝션 처리 어노테이션 import jakarta.transaction.Transactional;
public class MemberService {

    private final MemberRepository memberRepository;


    // List 변환 처리용 메소드
    private ArrayList<Member> toList(List<MemberEntity> list) {
        ArrayList<Member> memberList = new ArrayList<>();
        for (MemberEntity memberEntity : list) {
            memberList.add(memberEntity.toDto());
        }
        return memberList;
    }

    // 회원가입 처리 메소드
    public void insertMember(Member member) {
        try {
            memberRepository.save(member.toEntity());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // 로그인, 회원상세정보, 마이페이지 처리 메소드(uuid로 셀렉)
    public Member selectMember(String memUuId) {
        Optional<MemberEntity> entityOptional = memberRepository.findById(memUuId);
        return entityOptional.get().toDto();
    }

    // 아이디 중복확인 처리용 메소드
    public int selectCheckId(String memId) {
        if (memberRepository.selectCheckId(memId) > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    // 아이디 정보로 회원조회 메소드
    public Member findByMemId(String memId) {
        MemberEntity memberEntity = memberRepository.findByMemId(memId);
        return memberEntity.toDto();
    }

    // 회원탈퇴용 메소드
    public int removeByMemId(String memId) {
        return (int) memberRepository.removeByMemId(memId);
    }

    // 회원정보 수정 처리 메소드
    public int updateMember(Member member) {
        try {
            memberRepository.save(member.toEntity());
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return 0;
        }
    }

    // 회원 목록 출력 처리 메소드
    public List<Member> selectAllMember(Pageable pageable, Search search) {
        try {
            log.info("조회하는 값 확인(서비스) : {}", memberRepository.selectAllMember(pageable, search));
            return toList(memberRepository.selectAllMember(pageable, search));

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    // 리스트 페이징 및 검색 페이징 용 메소드
    public int selectAllCount() {
        return (int) memberRepository.selectAllCount();
    }

    public int selectMemIdCount(String keyword) {
        return (int) memberRepository.selectMemIdCount(keyword);
    }

    public int selectMemNameCount(String keyword) {
        return (int) memberRepository.selectMemNameCount(keyword);
    }

    public int selectMemStatusCount(String keyword) {
        return (int) memberRepository.selectMemStatusCount(keyword);
    }

    public int selectMemTypeCount(String keyword) {
        return (int) memberRepository.selectMemTypeCount(keyword);
    }

    // 아이디, 비밀번호 찾기 용 메소드
    public boolean findByEmailName(String memEmail, String memName) {
        return memberRepository.findByEmailName(memEmail, memName);
    }

    public boolean findByPhoneName(String memCellphone, String memName) {
        return memberRepository.findByPhoneName(memCellphone, memName);
    }

    public boolean findByEmailId(String memEmail, String memId) {
        return memberRepository.findByEmailId(memEmail, memId);
    }

    public  boolean findByPhoneId(String memCellphone, String memId) {
        return memberRepository.findByPhoneId(memCellphone, memId);
    }

    public Member findByMemName(String memName) {
        try {
            return memberRepository.findByMemName(memName).toDto();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public int updateMemPw(String memPw, String memUUID) {
        return (int) memberRepository.updateMemPw(memPw, memUUID);
    }

    // 소셜 로그인 관련 메소드
    public Member findBySocialPi(String provider ,String SocialPi) {
        MemberEntity memberEntity = memberRepository.findBySocialPi(provider, SocialPi);
        if(memberEntity != null) {
            return memberEntity.toDto();
        } else {
            return null;
        }
    }

    public int updateSocial(Boolean linking, String provider, String socialPi, String memUUID) {
        return (int) memberRepository.updateSocial(linking, provider, socialPi, memUUID);
    }

    public Member findByProfile(String memSeniorProfile) {
        MemberEntity memberEntity = memberRepository.findByProfile(memSeniorProfile);
        if(memberEntity != null) {
            return memberEntity.toDto();
        } else {
            return null;
        }
    }

    // 어르신 목록 출력 처리 메소드
    public List<Member> selectAllSenior(Pageable pageable, Search search, String memUUID, String type) {
        try {
            log.info("조회하는 값 확인(서비스)(selectAllSenior) : {}", memberRepository.selectAllSenior(pageable, search, memUUID, type));
            return toList(memberRepository.selectAllSenior(pageable, search, memUUID, type));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }
    //25-01-11(수진이가 수정)
    // 대시보드용 어르신 전체 카운트 메소드
    public int selectAllSeniorCount(String memUUID, String memType) {
        return memberRepository.selectAllSeniorCount(memUUID,memType);
    }



    // 검색 조건에 따라 카운트 메소드
    public int selectSeniorCount(String keyword,String memUUID, String action, String type) {
        return (int) memberRepository.selectSeniorCount(keyword,memUUID, action, type);
    }

    // 가족계정 승인처리 메소드
    public int updateApproval(String memUUID, String status) {
        return (int) memberRepository.updateApproval(memUUID, status);
    }

    // 담당자가 관리하는 어르신의 가족계정 승인여부 조회하는 메소드
    public int selectNeedApprovalCount(String memUUID) {
        return (int) memberRepository.selectNeedApprovalCount(memUUID);
    }

    // 가족 회원가입시 어르신 검색
    // 전체 검색 메소드
    public Map<String, Object> selectAllSeniorFam(Pageable pageable, Search search) {
        try {
            Map<String, Object> list = memberRepository.selectAllSeniorFam(pageable, search);
            log.info("조회하는 값 확인(서비스)(selectAllSeniorFam) : {}", list);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    // 전체 검색 목록 갯수 카운트
    public int selectAllSeniorFamCount() {
        return (int) memberRepository.selectAllSeniorFamCount();
    }

    // 이름 검색 목록 갯수 카운트
    public int selectSeniorNameFamCount(String keyword) {
        return (int) memberRepository.selectSeniorNameFamCount(keyword);
    }
    
    // 가족이 회원가입 시 선택한 어르신의 정보 수정 메소드
    public int updateSeniorFamApproval(String memUUID, String relationship, String memUUIDFam) {
        return (int) memberRepository.updateSeniorFamApproval(memUUID, relationship, memUUIDFam);
    }

    // 담당자가 관리하는 어르신의 가족계정 승인요청 목록
    public Map<String, Object> selectApprovalList(Pageable pageable, Search search, String memUUID) {
        try {
            Map<String, Object> list = memberRepository.selectApprovalList(pageable, search, memUUID);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public int selectApprovalCount(String memUUID) {
        return (int) memberRepository.selectApprovalCount(memUUID);
    }
}
