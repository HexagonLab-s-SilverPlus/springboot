package com.hexalab.silverplus.security.jwt.filter;

import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import com.hexalab.silverplus.member.model.service.MemberService;
import com.hexalab.silverplus.security.jwt.filter.output.CustomUserDetails;
import com.hexalab.silverplus.security.jwt.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
// Spring Security 가 제공하는 OncePerRequestFilter 를 상속받음
// OncePerRequestFilter : 모든 요청에 대해 한번씩 실행되는 필터임
public class JWTFilter extends OncePerRequestFilter {
    // JWT 관련 유틸리티 메소드를 제공하는 JWTUtil 인스턴스를 멤버로 선언
    private final JWTUtil jwtUtil;
    private final MemberService memberService;
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    private boolean isPassURL(String requestURI) {
        List<String> passURLs = Arrays.asList(
                "/login",       // 로그인 URL
                "/reissue",      // 토큰재발급 URL
                "/api/sms",     // 휴대전화인증번호 요청 URL
                "/api/sms/verify",      // 휴대전화인증 URL
                "/member/idchk",        // 아이디 중복체크 URL
                "/member/enroll",       // 회원가입 URL
                "/api/email",       // 이메일인증번호 요청 URL
               "/api/email/verify",      // 이메일인증 URL
                "/member/fid",       // 아이디 찾기 URL
                "/member/fpwd"     // 비밀번호 찾기 URL
        );
        return passURLs.contains(requestURI);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("JWT filter running...");

        String request_accessToken = request.getHeader("Authorization");
        String request_refreshToken = request.getHeader("RefreshToken");
        log.info("request_accessToken : {}", request_accessToken);
        log.info("request_refreshToken : {}", request_refreshToken);

        String requestURI = request.getRequestURI();
        log.info("requestURI : {}", requestURI);

        try {
            // 로그인 및 토큰 재발급 요청은 필터 통과
            if (isPassURL(requestURI)) {
                log.info("조건문 작동확인");
                filterChain.doFilter(request, response);
                return;
            }

            if (request_accessToken != null && request_refreshToken != null) {
                String accessToken = request_accessToken.substring("Bearer ".length());
                String refreshToken = request_refreshToken.substring("Bearer ".length());
                log.info("accessToken : {}", accessToken);
                log.info("refreshToken : {}", refreshToken);


                log.info("Checking AccessToken expiration...");
                boolean isAccessTokenExpired = jwtUtil.isTokenExpired(accessToken);
                log.info("AccessToken 만료 여부: {}", isAccessTokenExpired ? "만료됨" : "유효함");

                log.info("Checking RefreshToken expiration...");
                boolean isRefreshTokenExpired = jwtUtil.isTokenExpired(refreshToken);
                log.info("RefreshToken 만료 여부: {}", isRefreshTokenExpired ? "만료됨" : "유효함");

                // refreshToken 만료, accessToken 유효
                if (!isAccessTokenExpired && isRefreshTokenExpired) {
                    log.info("refreshToken-expired & accessToken-not expired");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setHeader("Access-Control-Expose-Headers", "Token-Expired");
                    response.setHeader("Token-Expired", "RefreshToken");
                    response.getWriter().write("{\"error\": \"RefreshToken expired\"}");
                    return;
                }

                // accessToken 만료, refreshToken 유효
                if (isAccessTokenExpired && !isRefreshTokenExpired) {
                    log.warn("accessToken-expired");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setHeader("Access-Control-Expose-Headers", "Token-Expired");
                    response.setHeader("Token-Expired", "AccessToken");
                    log.info("access 만료 & refresh 유효 응답 헤더값 : {}", response.getHeader("Token-Expired"));
                    response.getWriter().write("{\"error\": \"AccessToken expired\"}");
                    return;
                }

                // accessToken 만료, refreshToken 만료
                if (isAccessTokenExpired && isRefreshTokenExpired) {
                    log.warn("accessToken-expired & refreshToken-expired");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setHeader("Access-Control-Expose-Headers", "Token-Expired");
                    response.setHeader("Token-Expired", "AllToken");
                    log.info(response.getHeader("Token-Expired"));
                    response.getWriter().write("{\"error\": \"AllToken expired\"}");
                    return;
                }


                // 사용자 정보 추출
                String username = jwtUtil.getUserIdFromToken(accessToken);
//                String role = jwtUtil.getRoleFromToken(accessToken);

                log.info("username : {}", username);
                log.info("인증객체 생성 코드 작동 확인");
                // 사용자 인증 객체 생성
                MemberEntity member = memberService.findByMemId(username).toEntity();


                log.info("Authenticated member: {}", member);

                // ROLE_ Prefix 추가: Spring Security에서 권한을 인식하려면 ROLE_ prefix가 필요합니다.
                // SimpleGrantedAuthority: GrantedAuthority를 정확하게 설정합니다.
                // CustomUserDetails 및 Authentication 객체 생성
                CustomUserDetails customUserDetails = new CustomUserDetails(member);

//                Authentication authToken = new UsernamePasswordAuthenticationToken(
//                        customUserDetails,
//                        null
//                        , Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
//                );


                Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

                log.info(authToken.toString());
                // SecurityContext에 저장
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info(" 확인 : " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());

            } else {
                if (request_refreshToken == null) {
                    log.error("RefreshToken 헤더가 전달되지 않았습니다.");
                }
                log.info("전달온 Token 이 없음");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"invalid token\"}");
                return;
            }
            log.info("JWTFilter 제대로 작동 진행되는지 확인");
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT 처리 중 오류 발생: ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error\"}");
        }
    }
}

/*

            // 토큰 만료 여부 확인
            if (jwtUtil.isTokenExpired(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("token_expired");
                log.info("만료여부확인");
                return;
            }

            // 토큰 카테고리 확인
            String category = jwtUtil.getCategoryFromToken(token);
            if (!"access".equals(category)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid access token");
                log.info("카테고리확인");
                return;
            }

            // 사용자 정보 추출
            String username = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            // 사용자 인증 객체 생성
            MemberEntity member = new MemberEntity();
            member.setMemId(username);
            member.setMemType(role);
            member.setMemPw("tempPassword");

            log.info("Authenticated member: {}", member);

            // ROLE_ Prefix 추가: Spring Security에서 권한을 인식하려면 ROLE_ prefix가 필요합니다.
            // SimpleGrantedAuthority: GrantedAuthority를 정확하게 설정합니다.
            // CustomUserDetails 및 Authentication 객체 생성
            CustomUserDetails customUserDetails = new CustomUserDetails(member);

            Authentication authToken = new UsernamePasswordAuthenticationToken(
                    customUserDetails,
                    null
                    , Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
            );


            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

            log.info(authToken.toString());
            // SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.info(" 확인 : " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Access token expired");
        } catch (Exception e) {
            log.error("Error in JWTFilter: ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred during JWT processing.");
        }
    }

*/


