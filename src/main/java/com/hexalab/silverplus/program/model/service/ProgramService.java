package com.hexalab.silverplus.program.model.service;

import com.hexalab.silverplus.common.Search;
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
import java.util.Map;

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

    //Program List
    public int selectAllListCount() {
        return (int) programRepository.count();
    }//selectListCount end

    public int selectTitleListCount(String keyword) {
        return programRepository.selectTitleListCount(keyword);
    }

    public int selectContentListCount(String keyword) {
        return programRepository.selectContentListCount(keyword);
    }

    public int selectAreaListCount(String keyword) {
        return programRepository.selectAreaListCount(keyword);
    }

    public int selectOrgNameListCount(String keyword) {
        return programRepository.selectOrgNameListCount(keyword);
    }

    public int selectDateListCount(Search search) {
        return programRepository.selectDateListCount(search);
    }

    //Program search
    public Map<String, Object> selectSearchList(Pageable pageable, Search search) {
        return programRepository.selectSearchList(pageable, search);
    }

    //Program Detail
    public Program selectProgram(String snrProgramId) {
        return programRepository.findById(snrProgramId).get().toDto();
    }

    //Program update
    public int updateProgram(Program program) {
        try {
            programRepository.save(program.toEntity());
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ProgramService : ", e.getMessage());
            return 0;
        }
    }

    //Program delete
    public int deleteProgram(String snrProgramId) {
        try {
            programRepository.deleteById(snrProgramId);
            return 1; // 성공
        } catch (Exception e) {
            log.error("프로그램 삭제 실패:", e);
            return 0; // 실패
        }
    }
}//ProgramService end
