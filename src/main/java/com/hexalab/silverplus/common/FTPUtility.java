package com.hexalab.silverplus.common;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FTPUtility implements AutoCloseable {
    private FTPClient ftpClient;

    public void connect(String server, int port, String username, String password) throws IOException {
        ftpClient = new FTPClient();
        ftpClient.connect(server, port);
        ftpClient.login(username, password);
        ftpClient.enterLocalPassiveMode(); // 패시브 모드 설정
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE); // 바이너리 파일 타입 설정
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
