package com.hexalab.silverplus.notice.controller;

import com.hexalab.silverplus.notice.model.dto.Notice;
import com.hexalab.silverplus.notice.model.service.NoticeFilesService;
import com.hexalab.silverplus.notice.model.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
@CrossOrigin
public class NoticeController {

    // service DI
    private final NoticeService noticeService;
    private final NoticeFilesService noticeFilesService;


    // upload file path(./uploads)
    @Value("${uploadDir")
    private String uploadDir;

    // insert
    @PostMapping
    public ResponseEntity noticeInsert(
            @ModelAttribute Notice notice,
            @RequestParam(name="newfile",required = false) MultipartFile file
    ){
        // front data check
        log.info("notice data"+notice);
        log.info("newfile"+file);

        // notice insert
        try {
            noticeService.noticeInsert(notice);
            log.info("notice inserted");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


}
