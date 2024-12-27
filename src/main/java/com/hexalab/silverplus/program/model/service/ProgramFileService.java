package com.hexalab.silverplus.program.model.service;

import com.hexalab.silverplus.program.jpa.repository.ProgramFileRepository;
import com.hexalab.silverplus.program.jpa.repository.ProgramFileRepositoryCustom;
import com.hexalab.silverplus.program.model.dto.ProgramFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}//ProgramFileService end
