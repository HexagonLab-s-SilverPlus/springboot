package com.hexalab.silverplus.program.jpa.repository;

import com.hexalab.silverplus.program.jpa.entity.ProgramEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProgramRepository extends JpaRepository<ProgramEntity, UUID>, ProgramRepositoryCustom {
}
