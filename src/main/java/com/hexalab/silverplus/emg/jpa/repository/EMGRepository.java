package com.hexalab.silverplus.emg.jpa.repository;

import com.hexalab.silverplus.emg.jpa.entity.EMGEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EMGRepository extends JpaRepository<EMGEntity, String>, EMGRepositoryCustom {
}
