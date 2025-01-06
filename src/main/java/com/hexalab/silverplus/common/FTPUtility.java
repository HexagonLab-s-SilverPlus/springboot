package com.hexalab.silverplus.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
public class FTPUtility implements AutoCloseable {
    private FTPClient ftpClient;

    public void connect(String server, int port, String username, String password) throws IOException {
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(server, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            log.error("FTP 서버 연결 실패: {}", e.getMessage());
            throw e;
        }
    }


    public String[] search(String remoteFilePath){
        try {
            String[] fileNames = ftpClient.listNames( remoteFilePath);
            return fileNames;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void uploadFile(String localFilePath, String remoteFilePath) throws IOException {
        try (var inputStream = new FileInputStream(localFilePath)) {
            boolean success = ftpClient.storeFile(remoteFilePath, inputStream);
            if (!success) {
                throw new IOException("파일 업로드 실패: " + ftpClient.getReplyString());
            }
        }
    }

    public void downloadFile(String remoteFilePath, String localFilePath) throws IOException {
        try (var outputStream = new FileOutputStream(localFilePath)) {
            boolean success = ftpClient.retrieveFile(remoteFilePath, outputStream);
            if (!success) {
                throw new IOException("파일 다운로드 실패: " + ftpClient.getReplyString());
            }
        }
    }

    public void deleteFile(String remoteFilePath) throws IOException {
        boolean success = ftpClient.deleteFile(remoteFilePath);
        if (!success) {
            throw new IOException("파일 삭제 실패: " + ftpClient.getReplyString());
        }
    }

    @Override
    public void close() throws IOException {
        if (ftpClient != null && ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }
}
