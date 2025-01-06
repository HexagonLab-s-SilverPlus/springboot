package com.hexalab.silverplus.security.jwt.model.service;

import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import com.hexalab.silverplus.member.jpa.repository.MemberRepository;
import com.hexalab.silverplus.member.model.dto.Member;
import com.hexalab.silverplus.member.model.service.MemberService;
import com.hexalab.silverplus.security.exception.CustomException;
import com.hexalab.silverplus.security.jwt.filter.output.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
    LoginFilter 에서 두개의 서비스(@Service 로 등록된 클래스)를 등록한 경우
    authenticationManager 가 둘 중 사용할 서비스를 선택 못하는 문제가 발생함
    StackOverFlow : null 에러가 발생한 경우에 해결용으로 만드는 서비스 클래스임
    조건 : security 가 제공하는 UserDetailsService 인터페이스를 상속받은 후손 서비스 클래스를 만듦
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 데이터베이스에서 사용자 정보 조회해 옴
        Member member = memberRepository.findByMemId(username).toDto();
        if (member == null) {
            throw new UsernameNotFoundException("Find member is null : " + username);
        }

        switch (member.getMemStatus()) {
            case "REMOVE" -> {
                log.error("탈퇴 계정 예외 발생");
                throw new CustomException("탈퇴한 계정입니다.", HttpStatus.GONE, "REMOVE_ACCOUNT");
            }
            case "BLOCKED" -> {
                log.error("정지 계정 예외 발생");
                throw new CustomException("정지된 계정입니다.", HttpStatus.GONE, "BLOCKED_ACCOUNT");
            }
            case "INACTIVE" -> {
                log.error("휴면 계정 예외 발생");
                throw new CustomException("휴면 계정입니다.", HttpStatus.FORBIDDEN, "INACTIVE_ACCOUNT");
            }
        }

        // UserDetails 를 상속받은 CustomUserDetails 객체로 반환 처리
        return new CustomUserDetails(member.toEntity());
    }
}
