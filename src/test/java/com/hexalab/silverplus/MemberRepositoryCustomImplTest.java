package com.hexalab.silverplus.member.jpa.repository;

import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.config.QuerydslConfig;
import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import(QuerydslConfig.class)
class MemberRepositoryCustomImplTest {

    @Autowired
    private MemberRepositoryCustomImpl memberRepositoryCustom;

    @Autowired
    private JPAQueryFactory queryFactory;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10); // 페이지 설정
    }

    @Test
    void testSelectAllMember() {
        // 데이터 준비
        List<MemberEntity> members = memberRepositoryCustom.selectAllMember(pageable, new Search("all", "", null, null, 0, 10, 0));

        // 검증
        assertThat(members).isNotNull();
        assertThat(members.size()).isGreaterThan(0);
        for (MemberEntity member : members) {
            System.out.println("Member ID: " + member.getMemId() + ", Enroll Date: " + member.getMemEnrollDate());
            assertThat(member.getMemEnrollDate()).isNotNull();
        }
    }
}
