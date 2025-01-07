package com.hexalab.silverplus.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
    private final OAuth2AuthorizationRequestResolver defaultResolver;
    private final ObjectMapper objectMapper = new ObjectMapper();

//    public static final String ADDITIONAL_PARAMETERS = "additional_parameters";

    public CustomOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository, String authorizationRequestBaseUri) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, authorizationRequestBaseUri);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
        return customizeAuthorizationRequest(request, authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
        return customizeAuthorizationRequest(request, authorizationRequest);
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(HttpServletRequest request, OAuth2AuthorizationRequest authorizationRequest) {
        log.info("작동하는지 확인하기");

        if (authorizationRequest == null) {
            return null;
        }

        // 클라이언트 등록 ID를 가져오기
        String clientRegistrationId = (String) authorizationRequest.getAttributes().get("registration_id");
        if (clientRegistrationId == null) {
            log.error("client_registration_id가 null입니다. AuthorizationRequest: {}", authorizationRequest);
            throw new IllegalArgumentException("client_registration_id가 null입니다.");
        }

        // 요청에서 `linking`과 `memUUID` 쿼리 파라미터 가져오기
        String linking = request.getParameter("linking");
        String memUUID = request.getParameter("memUUID");

        log.info("값 확인 linking (OAuth2AuthorizationRequest) : {}", linking);
        log.info("값 확인 memUUID (OAuth2AuthorizationRequest) : {}", memUUID);

        // 네이버만 별도로 처리
        if ("naver".equalsIgnoreCase(clientRegistrationId)) {
            log.info("네이버 OAuth2 요청 - 매개변수 세션에 저장");

            // 추가 매개변수를 세션에 저장
            request.getSession().setAttribute("linking", linking);
            request.getSession().setAttribute("memUUID", memUUID);

            return authorizationRequest; // state는 수정하지 않음
        } else {
            // 기존 추가 매개변수를 복사
            Map<String, String> additionalState = new HashMap<>();
            if (linking != null) {
                additionalState.put("linking", linking);
            }
            if (memUUID != null) {
                additionalState.put("memUUID", memUUID);
            }

            try {

                String customState = objectMapper.writeValueAsString(additionalState);

                return OAuth2AuthorizationRequest.from(authorizationRequest)
                        .state(customState) // 기존 state 대신 직렬화된 추가 데이터를 포함
                        .build();
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize state", e);
            }
        }
    }
}

