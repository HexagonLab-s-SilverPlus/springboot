package com.hexalab.silverplus.book.controller;

import com.hexalab.silverplus.book.model.dto.Book;
import com.hexalab.silverplus.book.model.service.BookService;
import com.hexalab.silverplus.common.CreateRenameFileName;
import com.hexalab.silverplus.common.FTPUtility;
import com.hexalab.silverplus.common.Search;

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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.*;

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
//                if(search.getPageNumber()==0){
//                    search.setPageNumber(1);
//                    search.setPageSize(8);
//                }

                log.info("getPageSize : " + search.getPageSize());
                log.info("getPageSize : " + search.getPageNumber());

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

                // 파일담을 객체
                List<Map<String, Object>> fileList = new ArrayList<>();

                // FTP 서버 연결
                FTPUtility ftpUtility = new FTPUtility();
                ftpUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);

                // 파일 리스트 만들기
                for (Book book : bookList) {
                    Map<String, Object> fileData = new HashMap<>();
                    String originalFilename = book.getBookImage();
                    String uuid = book.getBookNum();

                    String mfRename = CreateRenameFileName.create(uuid,originalFilename);

                    // mfRename 값 확인
                    log.info("mfRename 값 확인: {}", mfRename);

                    // 파일 경로 구성
                    String remoteFilePath = ftpRemoteDir + "book/" + mfRename;
                    log.info("다운로드 시도 - 파일 경로: {}", remoteFilePath);

                    // 파일 다운로드
                    File tempFile = File.createTempFile("preview-", null);
                    ftpUtility.downloadFile(remoteFilePath, tempFile.getAbsolutePath());

                    // 파일 읽기
                    byte[] fileContent = Files.readAllBytes(tempFile.toPath());

                    tempFile.delete();

                    // MIME 타입 결정
                    String mimeType = getMimeType(book.getBookImage());
                    if (mimeType == null) {
                        mimeType = "application/octet-stream";
                    }

                    // text파일 텍스트 뽑아서 전송
                    String refilename = CreateRenameFileName.create(book.getBookNum(),book.getBookDetail());
                    String remotetextPath = ftpRemoteDir + "book/" + refilename;
                    File temptext = File.createTempFile("preview2-", null);
                    ftpUtility.downloadFile(remotetextPath, temptext.getAbsolutePath());
                    log.info("test1111111111111111111111111111111111111");
                    byte[] filetext = Files.readAllBytes(temptext.toPath());
                    log.info("test222222222222222222222222222222222222222");
                    String textContexnt = new String(filetext, StandardCharsets.UTF_8);
                    temptext.delete();
                    log.info(textContexnt);



                    // 파일 데이터 구성
                    fileData.put("book", book);
                    fileData.put("textContexnt", textContexnt);
                    fileData.put("fileName", book.getBookImage());
                    fileData.put("mimeType", mimeType);
                    fileData.put("fileContent", Base64.getEncoder().encodeToString(fileContent)); // Base64로 인코딩
                    fileList.add(fileData);
                }

                // map에 담아 전송
                Map<String,Object> map = new HashMap<>();
                map.put("fileList",fileList);
                //map.put("fileList",fileList);
                //map.put("list",bookList);
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

                // 파일 목록 만들기
                // 파일담을 객체
                List<Map<String, Object>> fileList = new ArrayList<>();

                // FTP 서버 연결
                FTPUtility ftpUtility = new FTPUtility();
                ftpUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);

                // 파일 리스트 만들기
                for (Book book : bookList) {
                    Map<String, Object> fileData = new HashMap<>();
                    String originalFilename = book.getBookImage();
                    String uuid = book.getBookNum();

                    String mfRename = CreateRenameFileName.create(uuid,originalFilename);

                    // mfRename 값 확인
                    log.info("mfRename 값 확인: {}", mfRename);

                    // 파일 경로 구성
                    String remoteFilePath = ftpRemoteDir + "book/" + mfRename;
                    log.info("다운로드 시도 - 파일 경로: {}", remoteFilePath);

                    // 파일 다운로드
                    File tempFile = File.createTempFile("preview-", null);
                    ftpUtility.downloadFile(remoteFilePath, tempFile.getAbsolutePath());

                    // 파일 읽기
                    byte[] fileContent = Files.readAllBytes(tempFile.toPath());

                    tempFile.delete();

                    // MIME 타입 결정
                    String mimeType = getMimeType(book.getBookImage());
                    if (mimeType == null) {
                        mimeType = "application/octet-stream";
                    }

                    // text파일 텍스트 뽑아서 전송
                    String refilename = CreateRenameFileName.create(book.getBookNum(),book.getBookDetail());
                    String remotetextPath = ftpRemoteDir + "book/" + refilename;
                    File temptext = File.createTempFile("preview2-", null);
                    ftpUtility.downloadFile(remotetextPath, temptext.getAbsolutePath());
                    log.info("test1111111111111111111111111111111111111");
                    byte[] filetext = Files.readAllBytes(temptext.toPath());
                    log.info("test222222222222222222222222222222222222222");
                    String textContexnt = new String(filetext, StandardCharsets.UTF_8);
                    temptext.delete();
                    log.info(textContexnt);

                    // 파일 데이터 구성
                    fileData.put("book", book);
                    fileData.put("textContexnt", textContexnt);
                    //fileData.put("uuid", book.getBookNum());
                    fileData.put("fileName", book.getBookImage());
                    fileData.put("mimeType", mimeType);
                    fileData.put("fileContent", Base64.getEncoder().encodeToString(fileContent)); // Base64로 인코딩
                    fileList.add(fileData);
                }

                Map<String,Object> map = new HashMap<>();
                map.put("fileList",fileList);
                //map.put("list",bookList);
                map.put("search",search);
                log.info("map : " + map);
                return ResponseEntity.ok(map);
            } catch (Exception e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    //detail
    @GetMapping("/{bookNum}")
    public ResponseEntity<Map> bookDetail(
            @PathVariable("bookNum") String bookNum
    ){
        log.info("bookNum : " + bookNum);

        // 담을객체
        Map<String,Object> map = new HashMap<>();
        try{
            // 북정보 불러오기
            Book book = bookService.selectBook(bookNum);
            log.info("book : " + book);

            // FTP 서버 연결
            FTPUtility ftpUtility = new FTPUtility();
            ftpUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);

            String originalFilename = book.getBookImage();
            String uuid = book.getBookNum();

            String mfRename = CreateRenameFileName.create(uuid,originalFilename);

            // mfRename 값 확인
            log.info("mfRename 값 확인: {}", mfRename);

            // 파일 경로 구성
            String remoteFilePath = ftpRemoteDir + "book/" + mfRename;
            log.info("다운로드 시도 - 파일 경로: {}", remoteFilePath);

            // 파일 다운로드
            File tempFile = File.createTempFile("preview-", null);
            ftpUtility.downloadFile(remoteFilePath, tempFile.getAbsolutePath());

            // 파일 읽기
            byte[] fileContent = Files.readAllBytes(tempFile.toPath());

            tempFile.delete();

            // MIME 타입 결정
            String mimeType = getMimeType(book.getBookImage());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            // 파일 데이터 구성
            map.put("book", book);
            //fileData.put("uuid", book.getBookNum());
            map.put("fileName", book.getBookImage());
            map.put("mimeType", mimeType);
            map.put("fileContent", Base64.getEncoder().encodeToString(fileContent)); // Base64로 인코딩

            return ResponseEntity.ok(map);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }

    @DeleteMapping("/{bookNum}")
    public ResponseEntity deleteBook(
            @PathVariable("bookNum") String bookNum,
            @RequestParam("text") String text,
            @RequestParam("image") String image
    ){
        log.info("bookNum : " + bookNum);
        try{
            if(bookService.bookDelete(bookNum)==1){
                // nas ftp connect
                FTPUtility ftpUtility = new FTPUtility();
                ftpUtility.connect(ftpServer,ftpPort,ftpUsername,ftpPassword);
                String textrename = CreateRenameFileName.create(bookNum,image);
                String imagename = CreateRenameFileName.create(bookNum,text);
                String textFilePath = ftpRemoteDir + "book/" + textrename;
                String imageFilePath = ftpRemoteDir + "book/" + imagename;
                ftpUtility.deleteFile(textFilePath);
                ftpUtility.deleteFile(imageFilePath);
                return ResponseEntity.ok().build();
            } else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch ( Exception e ){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    };


    @PutMapping("/{bookNum}")
    public ResponseEntity updateBook(
            @ModelAttribute Book book,
            @RequestParam(name="bookfile",required=false) MultipartFile bookfile,
            @RequestParam(name="bookimage",required=false) MultipartFile bookimage
    ){
        log.info("book : " + book);
        log.info("bookfile : " + bookfile);
        log.info("bookimage : " + bookimage);

        try{
            // nas ftp connect
            FTPUtility ftpUtility = new FTPUtility();
            ftpUtility.connect(ftpServer,ftpPort,ftpUsername,ftpPassword);
            if (bookfile!=null){
                // 기존파일 삭제
                String textrename = CreateRenameFileName.create(book.getBookNum(),book.getBookDetail());
                String textFilePath = ftpRemoteDir + "book/" + textrename;
                ftpUtility.deleteFile(textFilePath);
                // create file
                File tempFile1 = File.createTempFile("book-",null);
                // 파일변경
                bookfile.transferTo(tempFile1);
                // 파일이름 생성
                String renameFile1 = CreateRenameFileName.create(book.getBookNum(),bookfile.getOriginalFilename());
                // 패스설정
                String remoteFilePath1 = ftpRemoteDir + "book/"+renameFile1;
                // 파일 업로드
                ftpUtility.uploadFile(tempFile1.getAbsolutePath(),remoteFilePath1);
                // 임시파일 삭제
                tempFile1.delete();
                book.setBookDetail(bookfile.getOriginalFilename());
            }
            if (bookimage!=null){
                // 기존파일 삭제
                String imagename = CreateRenameFileName.create(book.getBookNum(),book.getBookImage());
                String imageFilePath = ftpRemoteDir + "book/" + imagename;
                ftpUtility.deleteFile(imageFilePath);
                // create file
                File tempFile1 = File.createTempFile("book-",null);
                // 파일변경
                bookimage.transferTo(tempFile1);
                // 파일이름 생성
                String renameFile1 = CreateRenameFileName.create(book.getBookNum(),bookimage.getOriginalFilename());
                // 패스설정
                String remoteFilePath1 = ftpRemoteDir + "book/"+renameFile1;
                // 파일 업로드
                ftpUtility.uploadFile(tempFile1.getAbsolutePath(),remoteFilePath1);
                // 임시파일 삭제
                tempFile1.delete();
                book.setBookImage(bookimage.getOriginalFilename());
            }
            book.setBookUpdateAt(new Timestamp(System.currentTimeMillis()));

            if (bookService.updatebook(book)==1){
                log.info("책등록 성공");
                return ResponseEntity.ok().build();
            } else{
                log.info("책 등록 실패");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    //MIME타입
    private String getMimeType(String snrFileOGName) {
        if (snrFileOGName == null || !snrFileOGName.contains(".")) {
            return null;
        }
        String extension = snrFileOGName.substring(snrFileOGName.lastIndexOf(".") + 1).toLowerCase();

        switch (extension) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
            case "tif":
            case "tiff":
                return "image/" + extension;
            case "xls":
            case "xlsx":
                return "application/vnd.ms-excel";
            case "pdf":
                return "application/pdf";
            case "txt":
                return "text/plain";
            case "hwp":
                return "application/x-hwp";
            case "hwpx":
                return "application/hwp+zip";
            case "doc":
            case "docx":
                return "application/msword";
            case "zip":
            case "rar":
            case "7z":
            case "tar":
            case "gz":
                return "application/zip";
            default:
                return "application/octet-stream"; // 기본 MIME 타입
        }
    }//getMimeType end
}
