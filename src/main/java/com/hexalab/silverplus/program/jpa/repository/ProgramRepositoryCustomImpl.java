package com.hexalab.silverplus.program.jpa.repository;

import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.program.jpa.entity.ProgramEntity;
import com.hexalab.silverplus.program.jpa.entity.QProgramEntity;
import com.hexalab.silverplus.program.model.dto.Program;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProgramRepositoryCustomImpl implements ProgramRepositoryCustom {
    //QueryDSL
    private final JPAQueryFactory queryFactory;
    //JPQL
    private final EntityManager entityManager;

    private QProgramEntity program = QProgramEntity.programEntity;

    @Override
    public int selectTitleListCount(String keyword) {
        return (int) queryFactory
                .selectFrom(program)
                .where(program.snrTitle.like("%" + keyword + "%"))
                .fetchCount();
    }//selectTitleListCount end

    @Override
    public int selectContentListCount(String keyword) {
        return (int) queryFactory
                .selectFrom(program)
                .where(program.snrContent.like("%" + keyword + "%"))
                .fetchCount();
    }

    @Override
    public int selectAreaListCount(String keyword) {
        return (int) queryFactory
                .selectFrom(program)
                .where(program.snrOrgAddress.like("%" + keyword + "%"))
                .fetchCount();
    }

    @Override
    public int selectOrgNameListCount(String keyword) {
        return (int) queryFactory
                .selectFrom(program)
                .where(program.snrOrgName.like("%" + keyword + "%"))
                .fetchCount();
    }

    @Override
    public int selectDateListCount(Search search) {
        return (int) queryFactory
                .selectFrom(program)
                .where(
                        program.snrStartedAt.loe(search.getEndDate())
                                .and(program.snrEndedAt.goe(search.getStartDate()))
                )
                .fetchCount();
    }

    @Override
    public Map<String, Object> selectSearchList(Pageable pageable, Search search) {
        List<ProgramEntity> programList = new ArrayList<>();

        if (search.getAction().equals("all")) {
            programList = queryFactory
                    .selectFrom(program)
                    .orderBy(program.snrCreatedAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if (search.getAction().equals("pgTitle")) {
            programList = queryFactory
                    .selectFrom(program)
                    .where(program.snrTitle.like("%" + search.getKeyword() + "%"))
                    .orderBy(program.snrCreatedAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if (search.getAction().equals("pgContent")) {
            programList = queryFactory
                    .selectFrom(program)
                    .where(program.snrContent.like("%" + search.getKeyword() + "%"))
                    .orderBy(program.snrCreatedAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if (search.getAction().equals("pgArea")) {
            programList = queryFactory
                    .selectFrom(program)
                    .where(program.snrOrgAddress.like("%" + search.getKeyword() + "%"))
                    .orderBy(program.snrCreatedAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if (search.getAction().equals("pgOrg")) {
            programList = queryFactory
                    .selectFrom(program)
                    .where(program.snrOrgName.like("%" + search.getKeyword() + "%"))
                    .orderBy(program.snrCreatedAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if (search.getAction().equals("pgDate")) {
            programList = queryFactory
                    .selectFrom(program)
                    .where(
                            program.snrStartedAt.loe(search.getEndDate())
                                    .and(program.snrEndedAt.goe(search.getStartDate()))
                    )
                    .orderBy(program.snrCreatedAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        }

        // ProgramEntity -> Program 변환
        List<Program> programs = programList.stream()
                .map(ProgramEntity::toDto) // 변환 메서드 호출
                .collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        map.put("list", programs);
        map.put("search", search);

        return map;
    }

}//ProgramRepositoryCustomImpl end
