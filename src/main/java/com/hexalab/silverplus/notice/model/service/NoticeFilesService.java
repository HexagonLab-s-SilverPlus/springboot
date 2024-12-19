package com.hexalab.silverplus.notice.model.service;

import com.hexalab.silverplus.notice.jpa.repository.NoticeFilesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NoticeFilesService {

    private final NoticeFilesRepository noticeFilesRepository;
}
