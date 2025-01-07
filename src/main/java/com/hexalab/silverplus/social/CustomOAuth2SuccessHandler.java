package com.hexalab.silverplus.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexalab.silverplus.member.model.dto.Member;
import com.hexalab.silverplus.member.model.service.MemberService;
import com.hexalab.silverplus.security.jwt.jpa.entity.RefreshToken;
import com.hexalab.silverplus.security.jwt.model.service.RefreshService;
import com.hexalab.silverplus.security.jwt.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberService memberService;
    private final long access_expiration;
    private final long refresh_expiration;
    private final JWTUtil jwtUtil;
    private final RefreshService refreshService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomOAuth2SuccessHandler(MemberService memberService, @Value("${jwt.access-token.expiration}") long access_expiration, @Value("${jwt.refresh-token.expiration}") long refresh_expiration, JWTUtil jwtUtil, RefreshService refreshService) {
        this.memberService = memberService;
        this.access_expiration = access_expiration;
        this.refresh_expiration = refresh_expiration;
        this.jwtUtil = jwtUtil;
        this.refreshService = refreshService;
    }

    // 소셜 로그인 처리 메소드
    private void socailLogin(Member member, HttpServletResponse response, HttpServletRequest request, String provider, String providerId) {
        try {
            if (member != null) {
                String access = jwtUtil.generateToken(member.getMemId(), "access", access_expiration);
                String refresh = jwtUtil.generateToken(member.getMemId(), "refresh", refresh_expiration);
                String linked = "on";

                RefreshToken refreshToken = RefreshToken.builder()
                        .tokenUuid(UUID.randomUUID().toString())
                        .tokenStatus("activated")
                        .tokenValue(refresh)
                        .tokenExpIn(refresh_expiration)
                        .tokenMemUuid(member.getMemUUID())
                        .build();
                refreshService.save(refreshToken);

                String redirectURL = String.format("http://localhost:3000/oauth2?linked=%s&accessToken=%s&refreshToken=%s", linked, access, refresh);

                getRedirectStrategy().sendRedirect(request, response, redirectURL);
            } else {    // 연동이 안되어있을 시
                String linked = "off";
                String redirectURL = String.format("http://localhost:3000/oauth2?provider=%s&socialId=%s&linked=%s", provider, providerId, linked);
                getRedirectStrategy().sendRedirect(request, response, redirectURL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    // 소셜 연동 처리 메소드
    private void socialLink(Boolean linking, String memUUID, String provider, Map<String, Object> attributes, HttpServletResponse response, HttpServletRequest request) throws IOException {
        String socialId = null;
        log.info("전달 온 데이터 체크(socialLink) memUUID : {}", memUUID);

        // 전달 온 데이터에서 소셜 고유 ID 추출
        switch (provider) {
            case "google" -> socialId = (String) attributes.get("sub");
            case "kakao", "naver" -> socialId = attributes.get("id").toString();
        }

        log.info("전달 온 데이터 체크(socialLink) socialId : {}", socialId);
        log.info("전달 온 데이터 체크(socialLink) linking : {}", linking);

        String redirectURL = "http://localhost:3000/myinfofamily";

        // DB 업데이트 처리
        if (linking && socialId != null && memberService.findBySocialPi(provider, socialId) == null) {
            log.info("소셜 연동 작동확인(socialLink)");
            int result = memberService.updateSocial(linking, provider, socialId, memUUID);
            log.info("연동 결과 확인(socialLink) : {}", result);
            getRedirectStrategy().sendRedirect(request, response, redirectURL);
        } else if (!linking && memberService.findBySocialPi(provider, socialId) != null) {
            log.info("소셜 연동해제 작동확인(socialLink)");
            int result = memberService.updateSocial(linking, provider, socialId, memUUID);
            log.info("연동 해제 결과 확인(socialLink) : {}", result);
            getRedirectStrategy().sendRedirect(request, response, redirectURL);
        } else if (linking && memberService.findBySocialPi(provider, socialId) != null) {
            log.info("잘못 작동하는지 확인하기()");
            return ;
        }

    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("소셜 로그인 핸들러 작동 확인");

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String provider = authToken.getAuthorizedClientRegistrationId();

        OAuth2User oAuth2User = authToken.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("요청 온 소셜 정보 확인(onAuthenticationSuccess) Attributes: {}", attributes);

        String state = request.getParameter("state");
        log.info("추출한 state 값: {}", state);

        if (provider.equals("naver")) {
            // 세션에서 추가 매개변수 복원
            String linking = (String) request.getSession().getAttribute("linking");
            String memUUID = (String) request.getSession().getAttribute("memUUID");

            log.info("세션에서 복원한 linking: {}", linking);
            log.info("세션에서 복원한 memUUID: {}", memUUID);

            // 추가 로직 실행
            if (linking != null && memUUID != null) {
                boolean isLinkingRequest = Boolean.parseBoolean(linking);
                socialLink(isLinkingRequest, memUUID, provider, attributes, response, request);

                // 복원 후 세션에서 삭제
                request.getSession().removeAttribute("linking");
                request.getSession().removeAttribute("memUUID");
                return;
            } else {
                log.warn("linking 또는 memUUID가 세션에서 null입니다.");
            }
        } else {
            if (state != null) {
                try {
                    Map<String, Object> additionalParameters = objectMapper.readValue(state, Map.class);
                    String linking = (String) additionalParameters.get("linking");
                    String memUUID = (String) additionalParameters.get("memUUID");

                    log.info("추출한 쿼리파라미터 값 확인 linking  (onAuthenticationSuccess) : {}", linking);
                    log.info("추출한 쿼리파라미터 값 확인 memUUID  (onAuthenticationSuccess) : {}", memUUID);

                    // 추가 로직 (e.g., 소셜 연동 처리)
                    if (linking != null && memUUID != null) {
                        // 소셜 연동 관련 로직 호출
                        log.info("소셜 연동 로직 작동");
                        boolean isLinkingRequest = Boolean.parseBoolean(linking);
                        socialLink(isLinkingRequest, memUUID, provider, attributes, response, request);
                        return;
                    }
                } catch (Exception e) {
                    log.error("State 파싱 실패: {}", e.getMessage(), e);
                }
            }
        }

        String providerId = null;

        switch (provider) {
            case "google" -> { // 구글 소셜 로그인 시도 시
                providerId = oAuth2User.getAttribute("sub");
                // 연동 여부 확인을 위한 DB 조회
                Member member = memberService.findBySocialPi(provider,providerId);
                socailLogin(member, response, request, provider, providerId);
            }
            case "naver" -> {  // 네이버 소셜 로그인 시도 시
                providerId = attributes.get("id").toString();

                if (providerId == null) {
                    throw new IllegalArgumentException("Provider ID가 null입니다.");
                }
                log.info("네이버 소셜 로그인 시도 확인(onAuthenticationSuccess) : {}", provider + "&" + providerId);
                // 연동 여부 확인을 위한 DB 조회
                Member member = memberService.findBySocialPi(provider,providerId);
                socailLogin(member, response, request, provider, providerId);
            }
            case "kakao" -> {  // 카카오 로그인 시도 시
                providerId = attributes.get("id").toString();
                // 연동 여부 확인을 위한 DB 조회
                Member member = memberService.findBySocialPi(provider,providerId);
                socailLogin(member, response, request, provider, providerId);
            }
        }
    }
}
