package com.hexalab.silverplus.social;

import com.hexalab.silverplus.member.model.dto.Member;
import com.hexalab.silverplus.member.model.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberService memberService;

//    public CustomOAuth2SuccessHandler(MemberService memberService) {
//        this.memberService = memberService;
//    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String provider = authToken.getAuthorizedClientRegistrationId();

        OAuth2User oAuth2User = authToken.getPrincipal();
        String providerId = oAuth2User.getAttribute("sub");

        try {
            if (provider.equals("google")) {
                Member member = memberService.findByGoogleProviderId(providerId);
                if (member != null) {
                    String memId = member.getMemId();
                    String memPw = member.getMemPw();
                    getRedirectStrategy().sendRedirect(request, response, "/oauth2");
                } else {
                    String redirectURL = "/oauth2?"
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }



    }
}
