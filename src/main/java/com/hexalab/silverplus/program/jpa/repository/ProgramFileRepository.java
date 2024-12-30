package com.hexalab.silverplus.program.jpa.repository;

import com.hexalab.silverplus.program.jpa.entity.ProgramFileEntity;
import com.hexalab.silverplus.program.model.dto.ProgramFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
public interface ProgramFileRepository extends JpaRepository<ProgramFileEntity, String>, ProgramFileRepositoryCustom {
    ArrayList<ProgramFileEntity> findBySnrProgramId(String snrProgramId);
}
