package com.hexalab.silverplus.security.jwt.util;

import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import com.hexalab.silverplus.member.jpa.repository.MemberRepository;
import com.hexalab.silverplus.member.model.dto.Member;
import com.hexalab.silverplus.member.model.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Component  // 스프링 부트 컨테이너에 의해 관리되는 컴포넌트로 선언
//@PropertySource("classpath:application.properties")      // application.properties 가 연결되지 않을 때 사용하는 어노테이션
public class JWTUtil {
    private final MemberRepository memberRepository;
    /*// JWT 생성과 검증에 사용될 비밀키(secret key) 만료시간을 필드로 선언함
    private SecretKeySpec secretKey;
    private final MemberService memberService;

    @Value("${jwt.secret}")
    private String secretKeyString;

    // 생성자를 통한 의존성 주입
    public JWTUtil(MemberService memberService) {
        this.memberService = memberService;
    }

    // application.properties 에 정의한 jwt 비밀키 값을 읽어와서 초기화하기 위해 @PostConstruct 설정해야 함
    @PostConstruct
    public void init() {
        // secretKey 초기화
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        this.secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    // JWT 토큰 생성 : 인증(authentication)시 받은 사용자 아이디를 전달받아서 확인하고 토큰 생성함
    // userId : 로그인 요청시 넘어온 회원 아이디 받음. category : 토큰의 종류(access token, refresh token)
    // expiredMs : 만료기한에 대한 밀리초
    public String generateToken(String userId, String category, Long expiredMs) {
        log.info("generate token : {}", secretKey);
        // Member Service 사용해서 db 에서 로그인한 사용자 정보를 조회해 옴
        Member member = memberService.selectMember(userId);

        // 사용자 정보가 없는 경우, UsernameNotFoundException (스프링 제공됨)을 발생시킴
        if (member == null) {
            throw new UsernameNotFoundException("userId : " + userId + "not found");
        }

        // 사용자의 관리자 여부 확인
        String adminYN = member.getAdminYN();

        // JWT 토큰 생성 : 사용자 아이디(subject)에 넣고, 관리자여부는 클레임으로 추가함 (임의대로 지정함)
        return Jwts.builder()
                .setSubject(userId) // 사용자 ID 설정 (로그인 시 이메일 사용시에는 이메일 등록)
                .claim("category", category)    // 카테고리 정보 추가 ("access", "refresh")
                .claim("name", member.getUserName())    // 사용자 이름 또는 닉네임 정보 추가
                .claim("role", (adminYN.equals("Y")? "ADMIN": "USER"))      // ROLE 정보 추가   (SecurityConfig 에서 지정한 명칭과 일치해야 함 - 대소문자 일치)
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))        // 토큰만료시간 설정        // import java.sql.Date
                .signWith(secretKey, SignatureAlgorithm.HS256)      // 비밀키와 알고리즘으로 서명
                .compact();     // JWT 생성 : JWT 를 압축 문자열로 만듦
    }

    // JWT 에서 사용자 아이디 추출하는 메소드
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    // JWT 의 만료 여부 확인용 메소드
    public Boolean isTokenExpired(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claims.getExpiration().before(new java.util.Date());     // true : 유효기간 지남 - 만료, false : 유효기간 남음 - 만료 안됨
    }

    // JWT 에서 관리자 여부 추출하는 메소드
    public String getAdminFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claims.get("role", String.class);       // get() 으로 꺼낼경우 기본 Object 형 이므로 형변환 필요
    }

    // JWT 에서 등록된 토큰 종류 추출하는 메소드
    public String getCategoryFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claims.get("category", String.class);
    }*/

    //JWT 생성과 검증에 사용될 비밀키와 만료시간을 필드로 선언함
    private SecretKeySpec secretKey;
    private final MemberService memberService;

    @Value("${jwt.secret}")
    private String secretKeyString;

    //생성자를 통한 의존성 주입
    public JWTUtil(MemberService memberService, MemberRepository memberRepository) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

    //application.porperties 에 정의한 jwt 비밀키 읽어와서 지정하기 위해 @PostConstruct 설정해야 함
    @PostConstruct
    public void init(){
        //secretKey 초기화
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        this.secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    //JWT 토큰 생성 : 인증(athentication)시 받은 사용자 아이디를 전달받아서 확인하고 토큰생성함
    //userId : 로그인 요청시 넘어온 회원 아이디 받음, category : 토큰의 종류(access, refresh),
    //expiredMs : 만료기한에 대한 밀리초
    public String generateToken(String memId, String category, Long expiredMs) {
        log.info("generate token : {}", secretKey);

        //MemberSerive 사용해서 db 에서 로그인한 사용자 정보를 조회해 옴
        Member member = memberService.findByMemId(memId);
        Optional<MemberEntity> mem = memberRepository.findById(member.getMemUUID());
        log.info("member : {}", member);
        log.info("mem : {}", mem);

        //사용자 정보가 없는 경우, UsernameNotFoundException (스프링 제공됨)을 발생시킴
        if (member == null) {
            throw new UsernameNotFoundException("userId : " + memId + "not found.");
        }

        //사용자의 관리자 여부 확인
        String memType = member.getMemType();
        log.info("enrolldate : {}", member.getMemEnrollDate());

        //JWT 토큰 생성 : 사용자 아이디(subject)에 넣고, 관리자여부는 클레임으로 추가함 (임의대로 지정함)
        return Jwts.builder()
                .setSubject(memId)  // 사용자 ID 설정 (로그인시 이메일 사용시에는 이메일 등록)
                .claim("category", category)  // 카테고리 정보 추가 ("access", "refresh")
                .claim("name", member.getMemName())  // 사용자 이름 또는 닉네임 추가
                .claim("role", (memType))  // ROLE 정보 추가.
                .claim("member", mem)    // 조회해 온 member 통째로 저장
                .setExpiration(new java.sql.Date(System.currentTimeMillis() + expiredMs))  // 토큰 만료 시간 설정
                .signWith(secretKey, SignatureAlgorithm.HS256)  // 비밀키와 알고리즘으로 서명
                .compact();  // JWT 생성 : JWT 를 압축 문자열로 만듦
    }

    // 공통 Claims 추출 메서드
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }

    public String getUserIdFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
//        log.info("만료여부 작동 확인");
//        return getClaimsFromToken(token).getExpiration().before(new java.util.Date());
        log.info("JWTUtil - 토큰 만료 여부 확인 시작: {}", token);

        if (token == null || token.trim().isEmpty()) {
            log.error("토큰이 비어있거나 유효하지 않습니다.");
            return true; // 만료된 것으로 간주
        }

        try {
            // JWT 파싱 및 Claims 추출
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token.trim())
                    .getBody();

            log.info("JWT 토큰 Claims: {}", claims);

            // 만료 여부 확인
            boolean isExpired = claims.getExpiration().before(new Date());
            log.info("JWT 토큰 만료 여부: {}", isExpired ? "만료됨" : "유효함");
            return isExpired;
        } catch (ExpiredJwtException e) {
            log.warn("토큰이 만료되었습니다: {}", e.getMessage());
            log.info("만료된 토큰 Claims: {}", e.getClaims()); // 만료된 Claims 정보 로그 출력
            return true; // 만료된 경우 true 반환
        } catch (IllegalArgumentException e) {
            log.error("JWT 파싱 중 오류: 토큰 형식이 잘못되었습니다. {}", e.getMessage());
            return true; // 오류 발생 시 만료로 간주
        } catch (Exception e) {
            log.error("JWT 파싱 중 예상치 못한 오류: {}", e.getMessage());
            return true; // 기타 오류도 만료로 간주
        }

    }

    public String getMemUuidFromToken(String token) {
        return getClaimsFromToken(token).get("member", Member.class).getMemUUID();
    }

    public String getCategoryFromToken(String token) {
        return getClaimsFromToken(token).get("category", String.class);
    }

    public String getRoleFromToken(String token) {
        return getClaimsFromToken(token).get("role", String.class);
    }


}
