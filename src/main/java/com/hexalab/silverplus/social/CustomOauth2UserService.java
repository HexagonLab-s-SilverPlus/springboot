//package com.hexalab.silverplus.social;
//
//import com.hexalab.silverplus.member.jpa.repository.MemberRepository;
//import com.hexalab.silverplus.member.model.dto.Member;
//import com.hexalab.silverplus.member.model.service.MemberService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class CustomOauth2UserService extends DefaultOAuth2UserService {
//    private final MemberRepository memberRepository;
//
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//        log.info("getAttributes : {}", oAuth2User.getAttributes());
//
//        String provider = userRequest.getClientRegistration().getRegistrationId();
//
//        OAuth2UserInfo oAuth2UserInfo = null;
//
//        if (provider.equals("google")) {
//            log.info("구글 로그인");
//            oAuth2UserInfo = new GoogleUserDetails(oAuth2User.getAttributes());
//
//            String providerId = oAuth2UserInfo.getProviderId();
//            String GoogleEmail = oAuth2UserInfo.getEmail();
//            String name = oAuth2UserInfo.getName();
//
//
//        }
//    }
//}
