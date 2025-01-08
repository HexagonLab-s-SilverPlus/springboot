package com.hexalab.silverplus.document.model.service;

import com.hexalab.silverplus.common.FTPUtility;
import com.hexalab.silverplus.document.jpa.entity.DocFileEntity;
import com.hexalab.silverplus.document.jpa.repository.DocFileRepository;
import com.hexalab.silverplus.document.model.dto.DocFile;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DocFileService {
    @Autowired
    private final DocFileRepository docFileRepository;
    @Autowired
    private FTPUtility ftPUtility;

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

    @Transactional
    public DocFile saveDocFile(String docId, MultipartFile file, String csvFilename) throws IOException {
        String originalFilename = file.getOriginalFilename();
        // renamedFilename = PK_OriginalFilename
        String dfId = UUID.randomUUID().toString();
        String renamedFilename = dfId + "_" + csvFilename;

        // 임시파일 생성 및 NAS 업로드
        // docfile-1234567890.tmp (접두사 뒤에 시스템에서 고유한 임의의 문자열 추가됨)
        // suffix(확장자)가 null이면 기본 확장자인 ".tmp"가 사용된다.
        // File.createTempFile은 운영체제가 제공하는 기본 임시 디렉토리에 파일을 생성한다.
        //Windows: C:\Users\<사용자>\AppData\Local\Temp

        File tempFile = File.createTempFile("docfile-", null);
        // NAS 업로드
        try {
            // MultipartFile을 임시 파일로 저장

            file.transferTo(tempFile);
            ftPUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);
            String remoteFilePath = ftpRemoteDir + "document/" + renamedFilename;
            // 임시 파일 생성 -> 메모리에만 존재하는 MultipartFile을 물리적인 파일로 변환해 FTP 업로드에 활용할 수 있다.
            // temp.getAbsolutePath()를 FTP 업로드 메소드에 전달한다,.
            //macOS/Linux: /tmp
            ftPUtility.uploadFile(tempFile.getAbsolutePath(), remoteFilePath);
            log.info("NAS에 파일 업로드 완료: {}", remoteFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("파일 업로드 중 오류 발생: {}", e.getMessage());
            throw e;
        } finally {
            // FTP 연결 종료
            ftPUtility.close();
            // FTP 업로드가 완료되면 더 이상 필요치 않은 임시 파일은 삭제
            // 로컬 스토리지 리소스 확보
            if (tempFile.exists()) {
                tempFile.delete();
                log.info("임시파일 삭제 완료: {}", tempFile.getAbsoluteFile());
            }
        }

        // DB 저장
        DocFileEntity docFileEntity = DocFileEntity.builder()
                .dfId(dfId)
                .dfOriginalName(originalFilename) // 실제 업로드된 원본 파일명
                .dfRename(renamedFilename)
                .docId(docId)
                .build();
        DocFileEntity savedEntity = docFileRepository.save(docFileEntity);
        log.info("파일 정보 저장 완료: {}", savedEntity);
        return savedEntity.toDto();
    }

    public DocFile getDocFileById(String dfId) {
        DocFileEntity entity = docFileRepository.findById(dfId)
                .orElseThrow(() -> new IllegalArgumentException("DocFile not found"));
        return entity.toDto();
    }


    /**
     * docId로 Document에 해당하는 파일 조회
     * @param docId
     * @return
     */
    public DocFile getDocFilesByDocId(String docId) {
        DocFileEntity docFileEntities = docFileRepository.findByDocId(docId);
        return docFileEntities.toDto();
    }


    @Transactional
    public void deleteDocFileById(String dfId) {
        if (!docFileRepository.existsById(dfId)) {
            throw new IllegalArgumentException("DocFile not found");
        }
        docFileRepository.deleteById(dfId);
        log.info("파일 삭제 완료: {}", dfId);
    }
}
