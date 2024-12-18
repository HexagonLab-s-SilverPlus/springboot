package com.hexalab.silverplus.security.jwt.filter.output;

import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
// Spring Security 에서 제공하는 UserDetails 인터페이스를 상속받아서 구현한 클래스
public class CustomUserDetails implements UserDetails {

    private final MemberEntity member;        // 사용자 정보를 담고 있는 Member 엔티티의 인스턴스임

    // 생성자를 이용한 의존성 주입
    public CustomUserDetails(MemberEntity member) {
        this.member = member;
    }

    // 사용자의 권한목록을 반환하는 메소드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

//        // 사용자 정보에서 관리자여부에 따라 ROLE 권한을 부여함
//        if (this.member.getAdminYN().equals("Y")) {
//            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//        } else {
//            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//        }
        return authorities;
    }

    // 사용자의 비밀번호 반환하는 메소드
    @Override
    public String getPassword() {
        return member.getMemPw();
    }

    // 사용자의 이름(로그인 시 사용된 아이디 또는 이메일)을 반환하는 메소드
    @Override
    public String getUsername() {
        return member.getMemId();
    }

    // 계정이 만료되었는지를 반환하는 메소드
    // JWT 토큰으로 기한만료를 확인할 것이므로, 인증에서는 만료되지 않았다고 처리함
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠겨있지 않는지를 반환하는 메소드
//    @Override
//    public boolean isAccountNonLocked() {
//        if (member.getLoginOk().equals("Y")) {
//            return true;        // 잠겨있지 않음
//        } else {
//            return false;       // 잠겨있음
//        }
//    }

    // 사용자의 크리덴셜(비밀번호 등)이 만료되지 않았는지를 반환하는 메소드
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 사용자 계정이 활성화(사용 가능) 상태인지를 반환하는 메소드
//    @Override
//    public boolean isEnabled() {
//        if (member.getLoginOk().equals("Y")) {
//            return true;        // 활성화
//        } else {
//            return false;       // 비활성화(disable)
//        }
    }
