package com.fyodor.ftp;

import com.fyodor.util.InputUtil;
import com.fyodor.util.log.Logger;

import java.io.*;

public class FileTransferManager {
    private static OutputStream dataOutputStream;
    private static final String MOCK_FILE_PATH = "/static/mock-students.json";

    public static void uploadMockFile(String remoteFilePath) {
        FtpClient ftpClient = FtpClient.getInstance();

        if (!ftpClient.isConnected()) {
            Logger.logWarning("Not connected to the server.");
            return;
        }

        try (InputStream resourceStream = FtpClient.class.getResourceAsStream(MOCK_FILE_PATH)) {
            ftpClient.sendCommand("STOR " + remoteFilePath + "\r\n");
            ftpClient.getResponse();
            OutputStream dataOutputStream;
            if (ftpClient.getDataTransferSocket() == null) {
                ftpClient.enablePassiveMode();
            }
            dataOutputStream = ftpClient.getDataTransferSocket().getOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead;
            if (resourceStream != null) {
                while ((bytesRead = resourceStream.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, bytesRead);
                }
            }
            dataOutputStream.flush();
            dataOutputStream.close();
            ftpClient.getResponse();
            Logger.logInfo("File " + remoteFilePath + " successfully uploaded to server.");
        } catch (FileNotFoundException e) {
            Logger.logWarning("Local file not found.");
            e.printStackTrace();
        } catch (IOException e) {
            Logger.logError("File upload failed.");
            e.printStackTrace();
        }
    }

    public static void uploadModifiedFile(ByteArrayOutputStream outputStream) {
        FtpClient ftpClient = FtpClient.getInstance();

        if (!ftpClient.isConnected()) {
            Logger.logWarning("Not connected to the server.");
            return;
        }

        try {
            String currentFilePath = ftpClient.getCurrentFilePath();
            if (currentFilePath != null) {
                ftpClient.sendCommand("STOR " + currentFilePath + "\r\n");
                String response = ftpClient.getResponse();
                if (response.startsWith("4")) {
                    ftpClient.enablePassiveMode();
                    ftpClient.sendCommand("STOR " + currentFilePath + "\r\n");
                    response = ftpClient.getResponse();
                    Logger.logServerResponse(response);
                }
                dataOutputStream = ftpClient.getDataTransferSocket().getOutputStream();
                dataOutputStream.write(outputStream.toByteArray());
                dataOutputStream.flush();
                dataOutputStream.close();
                ftpClient.getResponse();
                Logger.logInfo("File " + currentFilePath + " successfully uploaded to server.");
            }


        } catch (FileNotFoundException e) {
            Logger.logWarning("Local file not found.");
            e.printStackTrace();
        } catch (IOException e) {
            Logger.logError("File upload failed.");
            e.printStackTrace();
        }
    }

    public static void downloadRemoteFile() {
        String remoteFilePath = InputUtil.readFilePath();
        FtpClient ftpClient = FtpClient.getInstance();

        if (!ftpClient.isConnected()) {
            Logger.logWarning("Not connected to the server.");
            return;
        }

        try {
            ftpClient.sendCommand("RETR " + remoteFilePath + "\r\n");
            String response = ftpClient.getResponse();

            if (response.startsWith("4")) {
                ftpClient.enablePassiveMode();
                ftpClient.sendCommand("RETR " + remoteFilePath + "\r\n");
                response = ftpClient.getResponse();
            }
            if (response.startsWith("1")) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                InputStream dataInputStream = ftpClient.getDataTransferSocket().getInputStream();

                byte[] data = new byte[4096];
                int bytesRead;
                while ((bytesRead = dataInputStream.read(data)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }
                dataInputStream.close();

                response = ftpClient.getResponse();
                if (response.startsWith("2")) {
                    ftpClient.setFileBuffer(buffer);
                    ftpClient.setCurrentFilePath(remoteFilePath);
                    Logger.logInfo("File " + remoteFilePath + " successfully downloaded!");
                }
                else {
                    Logger.logError("File not downloaded!");
                }

            }
            else if (response.startsWith("5")) {
                System.out.println("\nТакого файла не существует");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void displayRemoteFiles() {
        FtpClient ftpClient = FtpClient.getInstance();

        if (!ftpClient.isConnected()) {
            Logger.logWarning("Not connected to the server.");
            return;
        }

        try {
            ftpClient.sendCommand("LIST\r\n");
            String response = ftpClient.getResponse();

            if (response.startsWith("150")) {

                BufferedReader dataReader = new BufferedReader(new InputStreamReader(ftpClient.getDataTransferSocket().getInputStream()));
                String line;
                while ((line = dataReader.readLine()) != null) {
                    System.out.println(line);
                }

                dataReader.close();

                ftpClient.getResponse();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
