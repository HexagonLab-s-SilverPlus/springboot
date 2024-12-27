package com.hexalab.silverplus.member.model.service;

import com.hexalab.silverplus.member.jpa.entity.MemberFilesEntity;
import com.hexalab.silverplus.member.jpa.repository.MemberFilesRepository;
import com.hexalab.silverplus.member.model.dto.MemberFiles;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional      // 트랜젝션 처리 어노테이션 import jakarta.transaction.Transactional;
public class MemberFilesService {

    private final MemberFilesRepository memberFilesRepository;

    public int insertMemberFiles(MemberFiles memberFiles) {
        try {
            memberFilesRepository.save(memberFiles.toEntity());
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return 0;
        }
    }

    public List<MemberFilesEntity> findByMemUuid(String memUuid) {
        try {
            List<MemberFilesEntity> list = memberFilesRepository.findByMemUuid(memUuid);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
