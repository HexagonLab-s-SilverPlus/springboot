package com.hexalab.silverplus.emg.model.service;

import com.hexalab.silverplus.emg.jpa.repository.EMGRepository;
import com.hexalab.silverplus.emg.model.dto.EMG;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor
@Transactional
public class EMGService {
    private final EMGRepository emgRepository;

    public List<EMG> selectAll(String uuid, Pageable pageable) {
        return emgRepository.searchIdAll(uuid, pageable);

    }

    public int selectCount(String uuid) {
        return emgRepository.selectCountId(uuid);
    }
}
