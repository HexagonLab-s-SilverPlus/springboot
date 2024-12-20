package com.hexalab.silverplus.security.controller;

import com.hexalab.silverplus.member.model.dto.Member;
import com.hexalab.silverplus.member.model.service.MemberService;
import com.hexalab.silverplus.security.jwt.jpa.entity.RefreshToken;
import com.hexalab.silverplus.security.jwt.model.service.RefreshService;
import com.hexalab.silverplus.security.jwt.model.service.UserService;
import com.hexalab.silverplus.security.jwt.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController("/token")
public class ReissueController {

    private final JWTUtil jwtUtil;      // jwt 토큰 처리를 위한 유틸리티
    private final MemberService memberService;      // 사용자 정보 확인 처리용
    private final RefreshService refreshService;        // 리프레시 토큰 처리용



    private final long access_expiration;
    private final long refresh_expiration;
    private final UserService userService;

    public ReissueController(JWTUtil jwtUtil,
                             MemberService memberService,
                             RefreshService refreshService,
                             @Value("${jwt.access-token.expiration}") long access_expiration,
                             @Value("${jwt.refresh-token.expiration}") long refresh_expiration, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.memberService = memberService;
        this.refreshService = refreshService;
        this.access_expiration = access_expiration;
        this.refresh_expiration = refresh_expiration;
        this.userService = userService;
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        log.info("reissueController 실행 확인");
        // request header에 보낸 accesstoken, refreshtoken 추출
        String request_accessToken = request.getHeader("Authorization");
        String request_refreshToken = request.getHeader("RefreshToken");

        // request header로 보낸 로그인 연장 요청 정보 추출
        String extendLogin = request.getHeader("extendLogin");

        log.info("전달온 accessToken 확인 : {}", request_accessToken);
        log.info("전달온 refreshToken 확인 : {}", request_refreshToken);

        try {
            // 추출한 accesstoken 과 refreshtoken 에서 'Bearer ' 제거
            String accessToken = request_accessToken != null && request_accessToken.startsWith("Bearer ") ? request_accessToken.substring("Bearer ".length()).trim() : null;
            String refreshToken = request_refreshToken != null && request_refreshToken.startsWith("Bearer ") ? request_refreshToken.substring("Bearer ".length()).trim() : null;

            // 토큰값이 null 일 경우 오류 반환
            if (accessToken == null || refreshToken == null) {
                log.warn("accessToken or refreshToken is null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            // 토큰 만료 여부 확인
            // accessToken
            boolean isAccessTokenExpired = jwtUtil.isTokenExpired(accessToken);     // 만료시 true 를 리턴함
            log.info("accessToken 만료여부 : {}", isAccessTokenExpired ? "만료" : "유효");
            // refreshToken
            boolean isRefreshTokenExpired = jwtUtil.isTokenExpired(refreshToken);       // 만료시 true 를 리턴함
            log.info("refreshToken 만료여부 : {}", isRefreshTokenExpired ? "만료" : "유효");

            // reissue 조건 1 :
            // accessToken 이 만료되고 refreshToken 이 유효한 경우
            if (isAccessTokenExpired && !isRefreshTokenExpired) {
                log.info("refreshToken 을 이용하여 accessToken 발급 진행");

                // refreshToken 정보에서 아이디 추출 (JWTUtil 클래스 안에 있는 메소드 사용)
                String memId = jwtUtil.getUserIdFromToken(refreshToken);

                // 추출한 아이디를 이용하여 accessToken 발급
                String newAccessToken = jwtUtil.generateToken(memId, "access", access_expiration);
                log.info("newAccessToken : {}", newAccessToken);

                // 발급된 accessToken 을 header 에 저장
                response.setHeader("Authorization", "Bearer " + newAccessToken);

                return ResponseEntity.ok("accessToken generated");
            }

            // reissue 조건 2:
            // accessToken 이 유효하고 refreshToken 이 만료된 경우
            if (isRefreshTokenExpired && !isAccessTokenExpired) {
                // 로그인 연장 여부에 대한 요청처리
                if ("true".equalsIgnoreCase(extendLogin)) {
                    log.info("로그인 연장 요청 처리");
                    // accessToken 정보에 있는 memId 추출
                    String memId = jwtUtil.getUserIdFromToken(accessToken);
                    // refreshToken 재발급 진행
                    String newRefreshToken = jwtUtil.generateToken(memId, "refresh", refresh_expiration);
                    response.setHeader("refreshToken", "Bearer " + newRefreshToken);
                    return ResponseEntity.ok("refreshToken generated");
                } else {
                    log.warn("로그인 연장 요청 안함");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("session expired");
                }
            }

            // reissue 조건 3:
            // accessToken 과 refreshToken 둘다 만료된 경우
            if (isRefreshTokenExpired && isAccessTokenExpired) {
                log.warn("토큰 둘 다 만료");
                // accessToken 정보안의  memUuid 추출
                String memUuid = jwtUtil.getMemUuidFromToken(accessToken);
                refreshService.deleteByRefreshTokenUuid(memUuid);
                log.info("리프레시토큰 삭제 완료");
                return ResponseEntity.badRequest().body("invalid token state");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }






/*
        if(refresh == null || !refresh.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String token = refresh.substring("Bearer ".length());

        // 토큰 만료여부 검사
        try {
            if(jwtUtil.isTokenExpired(token)) {
                // 리프레시 토큰이 만료되었다면 데이터베이스에서 기존 리프레시 토큰을 삭제
                refreshService.deleteByRefreshToken(token);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (ExpiredJwtException e) {
            refreshService.deleteByRefreshToken(token);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // 리프레시 토큰이 맞는지 카테고리로 확인
        String category = jwtUtil.getCategoryFromToken(token);
        if (category.equals("refresh")) {
            // 토큰에서 사용자 아이디 추출
            String username = jwtUtil.getUserIdFromToken(token);
            Member member = memberService.selectMember(username);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Optional<RefreshToken> refreshToken = refreshService.findByTokenValue(token);
            if (refreshToken.isEmpty()){
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                return new ResponseEntity<String>("refresh token not found or does not match", HttpStatus.BAD_REQUEST);
            }

            // 리프레시 토큰의 상태 확인
            RefreshToken refreshToken1 = refreshToken.get();
            if (!refreshToken1.getTokenStatus().equals("activated")) {
                return new ResponseEntity<>("refresh token is not activated", HttpStatus.BAD_REQUEST);
            }

            // 리프레시 토큰이 정상이면, 엑세스 토큰만 새로 생성함
            String access = jwtUtil.generateToken(username, "access", access_expiration);


            // 응답 객체에 새로운 엑세스 토큰 정보 추가
            response.setHeader("Authorization", "Bearer " + access);
            // 클라이언트가 Authorization 헤더를 읽을 수 있도록 설정
            response.setHeader("Access-Control-Expose-Headers", "Authorization");

            // 성공적으로 새 토큰을 받았을 때의 응답 처리
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.OK);
        */


    // access token 확인하고 처리하는 메소드
    private ResponseEntity<?> handleAccessTokenOnly(String token, HttpServletResponse response) {
        try {
            if(jwtUtil.isTokenExpired(token)) {
                return new ResponseEntity<>("access token expired", HttpStatus.UNAUTHORIZED);
            }
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("access token expired", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtUtil.getUserIdFromToken(token);
        Member member = memberService.selectMember(username);
        if (member == null) {
            return new ResponseEntity<>("no member found", HttpStatus.NOT_FOUND);
        }

        // access token 이 유효하다면 필요한 추가 처리를 진행
        // 예 : 사용자의 세션 갱신 또는 기타 액션

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
