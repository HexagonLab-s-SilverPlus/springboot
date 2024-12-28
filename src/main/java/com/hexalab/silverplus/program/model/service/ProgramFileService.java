package com.hexalab.silverplus.program.model.service;

import com.hexalab.silverplus.program.jpa.entity.ProgramFileEntity;
import com.hexalab.silverplus.program.jpa.repository.ProgramFileRepository;
import com.hexalab.silverplus.program.jpa.repository.ProgramFileRepositoryCustom;
import com.hexalab.silverplus.program.model.dto.ProgramFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProgramFileService {
    private final ProgramFileRepository programFileRepository;

    public int insertProgramFile(ProgramFile programFile) {
        try {
            programFileRepository.save(programFile.toEntity());
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return 0;
        }
    }//insertProgramFile end

    public ArrayList<ProgramFile> selectProgramFiles(String snrProgramId) {
        return programFileRepository.findBySnrProgramId(snrProgramId)
                .stream().map(ProgramFileEntity::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}//ProgramFileService end
