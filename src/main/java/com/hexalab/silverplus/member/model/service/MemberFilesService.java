package com.hexalab.silverplus.member.model.service;

import com.hexalab.silverplus.member.jpa.repository.MemberFilesRepository;
import com.hexalab.silverplus.member.model.dto.MemberFiles;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional      // 트랜젝션 처리 어노테이션 import jakarta.transaction.Transactional;
public class MemberFilesService {

    private final MemberFilesRepository memberFilesRepository;

    public int insertMemberFiles(MemberFiles memberFiles) {

        return 1;
    }
}
