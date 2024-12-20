package com.hexalab.silverplus.security.handler;

import com.hexalab.silverplus.security.jwt.filter.output.CustomUserDetails;
import com.hexalab.silverplus.security.jwt.jpa.entity.RefreshToken;
import com.hexalab.silverplus.security.jwt.model.service.RefreshService;
import com.hexalab.silverplus.security.jwt.model.service.UserService;
import com.hexalab.silverplus.security.jwt.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.util.Optional;

@Slf4j
//@RequiredArgsConstructor        // 자동 의존성 주입
public class CustomLogoutHandler implements LogoutHandler {
    private final JWTUtil jwtUtil;
    private final UserService userService;
    private final RefreshService refreshService;

    // 의존성 주입을 위한 매개변수 있는 생성자 직접 작성
    public CustomLogoutHandler(JWTUtil jwtUtil, RefreshService refreshService, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.refreshService = refreshService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        try {
            // request header 에서 'Authorization: Bearer <token 문자열>' 토큰 문자열 추출
            log.info("로그아웃 핸들러 작동확인");
            String authorization = request.getHeader("Authorization");
            log.info("requset 값 확인 : {}", request.getHeader("Authorization"));
            if (authorization != null && authorization.startsWith("Bearer ")) {
                // 'Bearer ' 다음부터 시작하는 실제 토큰 문자열 추출
                String token = authorization.substring("Bearer ".length());
                // 토큰 문자열에서 사용자 아이디 추출
                String userId = jwtUtil.getUserIdFromToken(token);
                // 사용자 아이디를 이용해서 사용자 정보 조회
                CustomUserDetails userDetails = (CustomUserDetails) userService.loadUserByUsername(userId);
                log.info("조회 작동 확인 : " + userDetails.toString());
                if (userDetails != null) {
                    // 해당 사용자의 Refresh-Token 을 db 에서 조회해 옴
                    Optional<RefreshToken> refresh = refreshService.findByTokenValue(token);
                    log.info("refresh : {}", refreshService.findByTokenValue(token));
                    if (refresh.isPresent()) {
                        RefreshToken refreshToken = refresh.get();
                        // 해당 리프레시 토큰을 db 에서 삭제
                        refreshService.deleteByRefreshToken(refreshToken.getTokenValue());
                        log.info("토큰 삭제 작동 확인");
                    }
                }
            }

            // 클라이언트에게 로그아웃 성공 응답을 보냄
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setHeader("Token-Expired", "RefreshToken");
        }
    }
}