package com.hexalab.silverplus.emg.controller;

import com.hexalab.silverplus.book.model.dto.Book;
import com.hexalab.silverplus.book.model.service.BookService;
import com.hexalab.silverplus.common.CreateRenameFileName;
import com.hexalab.silverplus.common.FTPUtility;
import com.hexalab.silverplus.common.Search;

import com.hexalab.silverplus.emg.model.dto.EMG;
import com.hexalab.silverplus.emg.model.service.EMGService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.*;

@Slf4j    //log 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@RequiredArgsConstructor
@RestController
@RequestMapping("/emg")
@CrossOrigin
public class EMGController {
    private final EMGService emgService;

    @GetMapping
    public ResponseEntity<Map> selectListEMG(
            @RequestParam String uuid,
            @ModelAttribute Search search
    ){
        Pageable pageable = PageRequest.of(search.getPageNumber() - 1,
                search.getPageSize(), Sort.by(Sort.Direction.DESC, "EMG_CANCEL"));


        List<EMG> emg = emgService.selectAll(uuid, pageable);
        search.setListCount(emgService.selectCount(uuid));
        Map<String, Object> map = new HashMap<>();
        map.put("list", emg);
        map.put("search", search);
        log.info("emg: {}", emg);
        log.info("search: {}", search);

        return ResponseEntity.ok(map);
    }
}
