package com.hexalab.silverplus.program.model.service;

import com.hexalab.silverplus.program.jpa.repository.ProgramRepository;
import com.hexalab.silverplus.program.model.dto.Program;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProgramService {
    private final ProgramRepository programRepository;

    //insert program
    public int insertProgram(Program program) {
        try {
            programRepository.save(program.toEntity());
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ProgramService : ", e.getMessage());
            return 0;
        }
    }//insertProgram end

}//ProgramService end
