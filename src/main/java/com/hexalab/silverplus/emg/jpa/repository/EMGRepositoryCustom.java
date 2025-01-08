package com.hexalab.silverplus.emg.jpa.repository;

import com.hexalab.silverplus.emg.model.dto.EMG;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EMGRepositoryCustom {
    List<EMG> searchIdAll(String uuid, Pageable pageable);

    int selectCountId(String uuid);
}
