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
                "reissue",      // 토큰재발급 URL
                "/api/sms",     // 휴대전화인증번호 요청 URL
                "/api/sms/verify",      // 휴대전화인증 URL
                "/member/idchk",        // 아이디 중복체크 URL
                "/member/enroll",       // 회원가입 URL
                "/api/email",       // 이메일인증번호 요청 URL
               "/api/email/verify"      // 이메일인증 URL
        );
        return passURLs.contains(requestURI);
    }

    // 생성자를 통한 의존성 주입
//    public JWTFilter(JWTUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }

    // 필터의 주요 로직을 구현하는 메소드임
/*    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 객체 (request) 에서 "Authorization" 헤더를 추출함
        String authorization = request.getHeader("Authorization");

        String requestURI = request.getRequestURI();
        if (requestURI.equals("/reissue")) {
            filterChain.doFilter(request, response);    // 현재 필터를 빠져나감
            return;
        }

        // 'Authorization' 헤더가 없거나 Bearer 뒤에 토큰이 아니면 요청을 계속 진행함
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 로그인 인증 정보(엑세스토큰)가 있다면 토큰을 추출함
//        String token = authorization.substring("Bearer ".length());
        String token = authorization.split(" ")[1];

        // 토큰 만료 여부 확인하고, 만료시에는 다음 필터로 넘기지 않음
        if (jwtUtil.isTokenExpired(token)){
            // response body
            PrintWriter out = response.getWriter();
            out.println("access token expired");
            // response status output
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;     // 서비스 요청한 클라이언트에게 엑세스 토큰 만료시한이 종료되었다고 메세지 보냄
        }

        // token 에서 category 가져오기
        String category = jwtUtil.getCategoryFromToken(token);
        // 토큰 카테고리가 'access' 가 아니라면 만료된 또는 잘못된 토큰으로 판단함
        if (!category.equals("access")) {
            // response body
            PrintWriter out = response.getWriter();
            out.println("invalid access token");
            // response status code output
            // 응답 상태 코드를 401 (SC_UNAUTHORIZED) 이 아닌 다른 코드로 약속하고 넘기면
            // 리프레시 토큰 발급 체크를 한다고 정하면 좀 더 빠르게 진행시킬 수 있음
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰에서 사용자 이름(아이디, 이메일) 과 관리자 여부를 추출함
        String username = jwtUtil.getUserIdFromToken(token);
        String admin = jwtUtil.getAdminFromToken(token);

        // 인증에 사용할 임시 MemberEntity (또는 Member) 객체를 생성하고 사용자이름과 관리자여부를 저장함
        MemberEntity member = new MemberEntity();
        member.setUserId(username);
        member.setAdminYN(admin.equals("ADMIN")? "Y" : "N");
        // 실제 인증에서는 사용되지 않는 임시 비밀번호 지정함
        member.setUserPwd("tempPassword");

        // CustomUserDetails 객체 생성해서 member 를 전달함
        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        // Spring Security 의 Authentication 객체를 생성하고, SecurityContext 에 저장함
        // 이로써 해당 요청에 대한 사용자 인증이 완료됨
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        // customUserDetails.getAuthorities() 인증토큰 받음
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 필터 체인을 계속 진행함 또는 실제 연결할 컨트롤러 메소드로 연결 처리함 (반드시 마지막에 들어가야 하는 코드구문)
        // 필터 진행이 계속되고 모든 필터가 완료가 되어 컨트롤러로 넘어가야 함
        filterChain.doFilter(request, response);
    }*/


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


