package com.hexalab.silverplus.member.model.service;

import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import com.hexalab.silverplus.member.jpa.repository.MemberRepository;
import com.hexalab.silverplus.member.model.dto.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional      // 트랜젝션 처리 어노테이션 import jakarta.transaction.Transactional;
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원가입 처리 메소드
    public int insertMember(Member member) {
        try {
            memberRepository.save(member.toEntity());
            return 1;
        } catch (Exception e) {
            log.error(e.getMessage());
            return 0;
        }
    }

    // 로그인, 회원상세정보, 마이페이지 처리 메소드
    public Member selectMember(String memId) {
        Optional<MemberEntity> entityOptional = memberRepository.findById(memId);
        return entityOptional.get().toDto();
    }


    /*
    // 회원정보 수정 처리 메소드
    public int updateMember() {}

    // 회원 목록 출력 처리 메소드
    public List<Member> selectAllMember() {}

    // 회원 탈퇴 처리 메소드
    public int removeMember() {}

    // 회원 상태정보 처리 메소드
    public int updateStatusMember() {}

    // 아이디 찾기 처리 메소드
    public String findMemberId(String memName){}

    // 비밀번호 찾기 처리 메소드
    public int findMemberPw(String memId, String memName){}*/
}
