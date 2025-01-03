package com.hexalab.silverplus.security.config;

import com.hexalab.silverplus.member.jpa.repository.MemberRepository;
import com.hexalab.silverplus.member.model.service.MemberService;
import com.hexalab.silverplus.security.handler.CustomLogoutHandler;
import com.hexalab.silverplus.security.jwt.filter.JWTFilter;
import com.hexalab.silverplus.security.jwt.filter.LoginFilter;
import com.hexalab.silverplus.security.jwt.model.service.RefreshService;
import com.hexalab.silverplus.security.jwt.model.service.UserService;
import com.hexalab.silverplus.security.jwt.util.JWTUtil;
import com.hexalab.silverplus.social.CustomOAuth2SuccessHandler;
//import com.hexalab.silverplus.social.CustomOauth2UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;

@Slf4j
@Configuration
@EnableWebSecurity
//@RequiredArgsConstructor    // 선언된 필드에 대한 자동 의존성 주입처리 하는 어노테이션
public class SecurityConfig {
    private final RefreshService refreshService;
    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
//    private final CustomOauth2UserService oauth2UserService;

    @Value("${jwt.access-token.expiration}")
    private long access_expiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refresh_expiration;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 직접 생성자를 작성해서 초기화 선언함 (@RequiredArgsConstructor 를 사용하지 않을 경우)
    public SecurityConfig(RefreshService refreshService, UserService userService, JWTUtil jwtUtil, MemberRepository memberRepository, MemberService memberService) {
        this.refreshService = refreshService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
//        this.oauth2UserService = oauth2UserService;
    }

//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    // 인증 (Authentication) 관리자를 스프링 부트 컨테이너에 Bean 으로 등록해야 함
    // 인증 과정에서 중요한 클래스임
    @Bean
    public AuthenticationManager authenticationManager() {
        // authenticationManager 두개의 서비스로 인해 StackOverFlow 가 발생한 경우
        // UserDetailsService 를 상속받은 서비스를 기본으로 사용하도록
        // 지정하는 코드로 변경함
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);        // UserDetailsService 구현체 (UserService) 사용을 지정함
        provider.setPasswordEncoder(bCryptPasswordEncoder());       // 패스워드 암호화 처리할 클래스 지정
        return new ProviderManager(provider);  // 다형성을 이용한 반환객체
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000"); // React 클라이언트 URL
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 인증 정보 허용
        configuration.addExposedHeader("Authorization");    // 클라이언트 쪽 헤더 접근 허용. * 사용못함
        configuration.addExposedHeader("Verify");
        configuration.addExposedHeader("Response");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    // HTTP 관련 보안 설정을 정의함
    // SecurityFilterChain 을 Bean 으로 등록하고, http 서비스 요청에 대한 보안 설정을 구성함
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2SuccessHandler customOAuth2SuccessHandler) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)      // import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
                .formLogin(AbstractHttpConfigurer::disable)     // 시큐리티가 제공하는 로그인 폼 사용 못하게 함
//                .httpBasic(AbstractHttpConfigurer::disable)     // form 태그로 submit 해서 오는 요청은 사용 못하게 함
                // 인증과 인가를 설정하는 부분

                .authorizeHttpRequests(auth -> {
                    Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication1 != null) {
                        System.out.println("==== SecurityContextHolder 정보 ====");
                    System.out.println("인증된 사용자: " + authentication1.getName());
                    System.out.println("권한: " + authentication1.getAuthorities());
                } else {
            System.out.println("SecurityContextHolder에 인증 정보가 없습니다.");
        } auth
                // 현재 프로젝트 안에 뷰 페이지를 작업할 때 설정하는 방식임 (리액트 작업시 제외)

                            // JWT 사용시 추가되는 설정임
                            .requestMatchers(  "/css/**", "/public/**", "/js/**", "/login", "/member/**", "/reissue",
                                                "/api/**", "/program/**", "/dashboard/**", "/qna/**").permitAll() // 공개 경로 설정 및 인증 경로 허용
                            // Notice
                            .requestMatchers(HttpMethod.POST, "/notice").hasRole("ADMIN")   // POST 요청은 ADMIN 롤 필요
                            .requestMatchers(HttpMethod.PUT, "/notice/{noticeNo}").hasRole("ADMIN")    // PUT 요청은 ADMIN 롤 필요
                            .requestMatchers(HttpMethod.DELETE, "/notice/{noticeNo}").hasRole("ADMIN") // DELETE 요청은 ADMIN 롤 필요

                            // QnA

                            // Program
                            .requestMatchers(HttpMethod.GET, "/program").hasAnyRole("ADMIN", "MANAGER",  "FAMILY", "SENIOR")
                            .requestMatchers(HttpMethod.POST, "/program").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.PUT, "/program/{snrProgramId}").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.DELETE, "/program/{snrProgramId}").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.GET, "/program/detail/{snrProgramId}").hasAnyRole("ADMIN", "MANAGER", "FAMILY", "SENIOR")
                            .requestMatchers(HttpMethod.GET, "/program/pfdown").hasAnyRole("ADMIN", "MANAGER", "FAMILY", "SENIOR")

                            // Dashboard
                            .requestMatchers(HttpMethod.POST, "/dashboard").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.GET, "/dashboard").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.DELETE, "/dashboard/{taskId}").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.PUT, "/dashboard/{taskId}").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.GET, "/dashboard/date/{taskDate}").hasAnyRole("ADMIN", "MANAGER")


                            // Book

                            // Workspace
                            .requestMatchers(HttpMethod.POST, "/api/workspace/create").hasAnyRole("ADMIN", "SENIOR")    // ADMIN 추후 삭제
                            .requestMatchers(HttpMethod.GET, "/api/workspace/{memUuid}/status").hasAnyRole("ADMIN", "SENIOR")  // TODO: (From.은영) 제가 수정했습니다. 노션에도 업뎃은 해놨어요. ADMIN 추후 삭제

                            // Chat
                            .requestMatchers(HttpMethod.POST, "/api/chat/save").hasAnyRole("ADMIN", "SENIOR")   // ADMIN 추후 삭제
                            .requestMatchers(HttpMethod.GET, "/api/chat/history/{workspaceId}").hasAnyRole("ADMIN", "SENIOR")   // ADMIN 추후 삭제

                            // Member
                            .requestMatchers(HttpMethod.GET, "/member/adminList").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/member/update/{memUUID}").hasRole("ADMIN")

                            // .permitAll() :  URL 의 접근을 허용한다는 의미(통과는 아님). 제일 처음 작동됨
                            // .permitAll() 에 등록되지 않은 url 은 서버에 접속 못하게 됨
                            // 로그 아웃 요청은 로그인한 사용자만 가능
                            .requestMatchers("/logout").authenticated()
                            // 위의 인가 설정을 제외한 나머지 요청들은 인증 거치도록 설정함
                            .anyRequest().authenticated();
                })
                // JWTFilter 와 LoginFilter 를 시큐리티 필터 체인에 추가 등록함
                .addFilterBefore(new JWTFilter(jwtUtil, memberService), LoginFilter.class)
                // UsernamePasswordAuthenticationFilter.class : LoginFilter 를 UsernamePasswordAuthenticationFilter 로 형변환 함
                .addFilterAt(new LoginFilter(userService, refreshService, memberRepository, authenticationManager(), jwtUtil, access_expiration, refresh_expiration), UsernamePasswordAuthenticationFilter.class)   // UsernamePasswordAuthenticationFilter : 스프링 부트에서 제공함
                // service 가 두개여서 authenticationManager 가 service 혼동 으로 인한 스택 오버플로우 발생
                // 로그아웃 처리는 커스터마이징함
                .logout(logout -> logout
                        .logoutUrl("/logout")   // 로그아웃 url 요청에 대한 앤드포인트 설정
                        .addLogoutHandler(new CustomLogoutHandler(jwtUtil, refreshService, userService))
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        }))
                // 세션 정책을 STATELESS 로 설정하고, 세션을 사용하지 않는 것을 명시함
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http
                .oauth2Login(oauth2 -> oauth2
                .successHandler(customOAuth2SuccessHandler));
        return http.build();
    }   // securityFilterChain

    //시큐리티 작동시 SecurityChainFilter 들이 순서대로 자동 작동되는지 확인
    //디버그용 필터
    static class DebugFilter implements Filter {
        private final String filterName;

        public DebugFilter(String filterName) {
            this.filterName = filterName;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            log.info("[DEBUG] Entering Filter : " + filterName);
            //현재 SecurityContext 상태 출력
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication != null) {
                log.info("[DEBUG] Authentication : " + authentication.getName()
                        + ", Authorization : " + authentication.getAuthorities());
            }else{
                log.info("[DEBUG] Authentication is null");
            }

            chain.doFilter(request, response);
            log.info("[DEBUG] Exiting Filter : " + filterName);
        }   // doFilter

    }   // DebugFilter

}
