package com.hexalab.silverplus.book.controller;

import com.hexalab.silverplus.book.model.dto.Book;
import com.hexalab.silverplus.book.model.service.BookService;
import com.hexalab.silverplus.common.CreateRenameFileName;
import com.hexalab.silverplus.common.FTPUtility;
import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.notice.model.dto.NoticeFiles;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/book")
@CrossOrigin
public class BookController {

    // service DI
    private final BookService bookService;

    // file upload path valiable
    @Value("${ftp.server}")
    private String ftpServer;
    @Value("${ftp.port}")
    private int ftpPort;
    @Value("${ftp.username}")
    private String ftpUsername;
    @Value("${ftp.password}")
    private String ftpPassword;
    @Value("${ftp.remote-dir}")
    private String ftpRemoteDir;

    // insert
    @PostMapping
    public ResponseEntity bookInsert(
            @ModelAttribute Book book,
            @RequestParam(name="bookfile") MultipartFile bookfile,
            @RequestParam(name="bookimage") MultipartFile bookimage
    ){
        log.info("book : " + book);
        log.info("bookfile : " + bookfile.getOriginalFilename());
        log.info("bookimage : " + bookimage.getOriginalFilename());

        //set(book)
        book.setBookNum(UUID.randomUUID().toString()); //UUID
        book.setBookCreateAt(new Timestamp(System.currentTimeMillis()));
        book.setBookUpdateAt(new Timestamp(System.currentTimeMillis()));
        book.setBookDetail(bookfile.getOriginalFilename());
        book.setBookImage(bookimage.getOriginalFilename());

        // upload file
        try{
            FTPUtility ftpUtility = new FTPUtility();
            ftpUtility.connect(ftpServer,ftpPort,ftpUsername,ftpPassword);

            // create file
            File tempFile1 = File.createTempFile("book1-",null);
            File tempFile2 = File.createTempFile("book2-",null);
            bookfile.transferTo(tempFile1);
            bookimage.transferTo(tempFile2);

            // 파일이름 생성
            String renameFile1 = CreateRenameFileName.create(book.getBookNum(),bookfile.getOriginalFilename());
            String renameFile2 = CreateRenameFileName.create(book.getBookNum(),bookimage.getOriginalFilename());
            // file upload
            String remoteFilePath1 = ftpRemoteDir + "book/"+renameFile1;
            String remoteFilePath2 = ftpRemoteDir + "book/"+renameFile2;

            ftpUtility.uploadFile(tempFile1.getAbsolutePath(),remoteFilePath1);
            ftpUtility.uploadFile(tempFile2.getAbsolutePath(),remoteFilePath2);

            // db save
            if (bookService.bookInsert(book) == 1 ){
                // delete tempFile
                tempFile1.delete();
                tempFile2.delete();
            } else {
                log.info("책 등록 실패");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e){
            e.printStackTrace();
            log.error("책 등록 중 오류발생 : ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // list
    @GetMapping
    public ResponseEntity<Map> bookList(
            @ModelAttribute Search search
    ){
        log.info("search : " + search);

        //검색조건 없을시
        if (search.getKeyword() == null|| search.getKeyword().isEmpty()){
            try{
                // list count
                int listCount = bookService.selectAllBookListCount();
                log.info("listCount : " + listCount);
                log.info("getPageNumber : " + search.getPageNumber());
                //search setting
                if(search.getPageNumber()==0){
                    search.setPageNumber(1);
                    search.setPageSize(8);
                }
                search.setListCount(listCount);

                //pageable 객체 생성
                Pageable pageable = PageRequest.of(
                        search.getPageNumber()-1,
                        search.getPageSize(),
                        Sort.by(Sort.Direction.DESC,"bookCreateAt")
                );
                // 목록조회
                ArrayList<Book> bookList = bookService.selectAllBookList(pageable);
                log.info("bookList count : " + bookList.size());

                // map에 담아 전송
                Map<String,Object> map = new HashMap<>();
                map.put("list",bookList);
                map.put("search",search);
                return ResponseEntity.ok(map);
            } catch (Exception e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            try{
                int listCount = bookService.selectSearchBookListCount(search.getKeyword());
                log.info("listCount : " + listCount);

                search.setListCount(listCount);

                // pageable 객체 생성
                Pageable pageable = PageRequest.of(
                        search.getPageNumber() -1,
                        search.getPageSize(),
                        Sort.by(Sort.Direction.DESC,"bookCreateAt")
                );

                // 목록조회
                ArrayList<Book> bookList = new ArrayList<Book>();
                bookList = bookService.selectSearchBookList(search.getKeyword(),pageable);
                log.info("bookList count : " + bookList.size());

                Map<String,Object> map = new HashMap<>();
                map.put("list",bookList);
                map.put("search",search);
                log.info("map : " + map);
                return ResponseEntity.ok(map);
            } catch (Exception e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}
