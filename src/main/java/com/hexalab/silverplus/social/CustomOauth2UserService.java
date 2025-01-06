package com.hexalab.silverplus.social;

import com.hexalab.silverplus.member.jpa.repository.MemberRepository;
import com.hexalab.silverplus.member.model.dto.Member;
import com.hexalab.silverplus.member.model.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        log.info("OAuth2UserService - Registration ID: {}", registrationId);
        log.info("OAuth2UserService - User Attributes: {}", oAuth2User.getAttributes());

        if ("naver".equals(registrationId)) {
            // 네이버는 사용자 정보를 'response' 필드 아래에 포함
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            if (response == null) {
                throw new IllegalArgumentException("네이버 응답에서 'response' 필드가 없습니다.");
            }

            // 디버깅 로그로 네이버 응답 확인
            System.out.println("네이버 사용자 정보: " + response);

            // 기본 ID 필드를 'id'로 설정
            return new DefaultOAuth2User(
                    oAuth2User.getAuthorities(),
                    response,
                    "id"
            );
        }

        return oAuth2User; // 다른 공급자는 기본 처리
    }
}
