package com.hexalab.silverplus.program.model.service;

import com.hexalab.silverplus.program.jpa.entity.ProgramEntity;
import com.hexalab.silverplus.program.jpa.repository.ProgramRepository;
import com.hexalab.silverplus.program.model.dto.Program;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProgramService {
    private final ProgramRepository programRepository;

    private ArrayList<Program> toList(Page<ProgramEntity> entityList) {
        ArrayList<Program> list = new ArrayList<>();
        for(ProgramEntity entity: entityList) {
            list.add(entity.toDto());
        }
        return list;
    }

    private ArrayList<Program> toList(List<ProgramEntity> entityList) {
        ArrayList<Program> list = new ArrayList<>();
        for(ProgramEntity entity: entityList) {
            list.add(entity.toDto());
        }
        return list;
    }

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

    public int selectListCount() {
        return (int) programRepository.count();
    }//selectListCount end

    public ArrayList<Program> selectList(Pageable pageable) {
        return toList(programRepository.findAll(pageable));
    }//selectList end
}//ProgramService end
