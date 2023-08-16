package com.fyodor.ftp;


import com.fyodor.model.Mode;
import com.fyodor.model.User;
import com.fyodor.util.InputUtil;
import com.fyodor.util.log.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class FtpClient {

    private String serverAddress;
    private int port;
    private static final FtpClient instance;
    private boolean isConnected = false;
    private String address;
    private User loggedInUser;

    private Socket commandSocket;

    private Socket dataTransferSocket;
    private BufferedReader serverResponseReader;
    private BufferedWriter serverCommandWriter;

    private Mode mode;

    private String currentFilePath;
    private ByteArrayOutputStream fileBuffer;

    private static final int FTP_COMMAND_PORT = 21;
    private static final int FTP_DATA_TRANSFER_PORT = 20;

    private OutputStream dataOutputStream;
    private FileInputStream fileInputStream;
    static {
        instance = new FtpClient();
    }

    private FtpClient() {
    }

    public static synchronized FtpClient getInstance() {
        return instance;
    }

    public void connect(String serverAddress, String username, String password) {
        try {
            address = serverAddress;
            commandSocket = new Socket(address, FTP_COMMAND_PORT);
            serverResponseReader = new BufferedReader(new InputStreamReader(commandSocket.getInputStream()));
            serverCommandWriter = new BufferedWriter(new OutputStreamWriter(commandSocket.getOutputStream()));

            String response = getResponse();
            if (response.startsWith("2")) {
                authenticate(username, password);
            } else {
                Logger.logWarning(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void authenticate(String username, String password) throws IOException {
        sendCommand("USER " + username + "\r\n");
        String response = getResponse();

        if (response.startsWith("3")) {
            sendCommand("PASS " + password + "\r\n");
            response = getResponse();

            if (response.startsWith("2")) {
                sendCommand("TYPE I\r\n");
                response = getResponse();

                isConnected = true;
            }
        }
    }


    protected void sendCommand(String command) {
        try {
            serverCommandWriter.write(command);
            serverCommandWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String getResponse() {
        String response = "";
        try {
            response = serverResponseReader.readLine();
            Logger.logServerResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public void disconnect() {
        if (commandSocket != null) {
            try {
                sendCommand("QUIT\r\n");
                String response = getResponse();
                if (response.startsWith("2")) {
                    System.out.println("Соединение с сервером остановлено");
                }
                commandSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        address = null;
        isConnected = false;
        mode = null;
    }

    public void setMode() {
        System.out.println("\n===== Выбор режима работы =====");
        System.out.println("1. Пассивный(PASV)");
        System.out.println("2. Активный(PORT)");
        System.out.println("3. Назад");
        int choice = InputUtil.getUserChoice(3);

        switch (choice) {
            case 1:
                setMode(Mode.PASSIVE);
                break;
            case 2:
                setMode(Mode.ACTIVE);
                break;
            case 3:
                System.out.println();
                break;
            default:
                Logger.logWarning("Invalid input!");
                break;
        }
    }

    public Socket getCommandSocket() {
        return commandSocket;
    }

    public  void setMode(Mode newMode) {
        if (mode == newMode) {
            Logger.logInfo("Mode is already set to " + newMode);
            return;
        }

        if (newMode == Mode.ACTIVE) {
            enableActiveMode();
        } else if (newMode == Mode.PASSIVE) {
            enablePassiveMode();
        }
    }

    private void enableActiveMode() {
        if (mode != Mode.ACTIVE) {
            try {
                System.out.println("\nК сожалению, активный режим не работает. NAT шлюз не пускает :(");
                Thread.sleep(2000);
//                String localAddress = InetAddress.getLocalHost().getHostAddress();
//                String localAddress = "134.122.66.69";
//                int localPort = 9999;
//                ServerSocket serverSocket = new ServerSocket(localPort);
//                Socket dataSocket = serverSocket.accept();

                // Формирование порта для команды PORT (в формате h1,h2,h3,h4,p1,p2)
//                String portCommand = String.format("%s,%s,%s,%s,%s,%s",
//                        localAddress.replace('.', ','), localPort / 256, localPort % 256);
//                String portCommand = "192,168,0,96,39,15";
//                System.out.println("Port command: " + portCommand);
//                serverCommandWriter.write("PORT " + portCommand);
//                serverCommandWriter.flush();
//                String response = serverResponseReader.readLine();
//                Logger.logServerResponse(response);
//                BufferedReader dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
//                PrintWriter dataWriter = new PrintWriter(dataSocket.getOutputStream(), true);

                // Пример чтения ответа от сервера
//                String response = dataReader.readLine();
//                System.out.println("Server response: " + response);
//                String response = serverResponseReader.readLine();
//                System.out.println("Server: " + response);
//                mode = Mode.ACTIVE;
//                serverSocket.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Logger.logInfo("Active mode is already set.");
        }
    }

    public void enablePassiveMode() {
            try {
                sendCommand("PASV\r\n");
                String response = getResponse();

                int startIndex = response.indexOf("(");
                int endIndex = response.indexOf(")");
                String extractedValues = response.substring(startIndex + 1, endIndex);

                String[] parts = extractedValues.split(",");
                serverAddress = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
                port = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);

                // Вывод сообщения о включении пассивного режима
                Logger.logInfo("Пассивный режим включен. Адрес: " + serverAddress + ", Порт: " + port);
                dataTransferSocket = new Socket(serverAddress, port);
                mode = Mode.PASSIVE;
            }
            catch (IOException e) {
                e.printStackTrace();
            }

    }

//    public void reconnectDataSocket() {
//        try {
//            dataTransferSocket = new Socket(serverAddress, port);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public boolean isConnected() {
        return isConnected;
    }

    public OutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public Socket getDataTransferSocket() {
        return dataTransferSocket;
    }

    public void closeAllResources() {
        try {
            if (commandSocket != null) commandSocket.close();
            if (dataTransferSocket != null) dataTransferSocket.close();
            if (serverCommandWriter != null) serverCommandWriter.close();
            if (serverResponseReader != null) serverResponseReader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
    public Mode getMode() {
        return mode;
    }

    public ByteArrayOutputStream getFileBuffer() {
        return fileBuffer;
    }

    public void setFileBuffer(ByteArrayOutputStream fileBuffer) {
        this.fileBuffer = fileBuffer;
    }

    public String getCurrentFilePath() {
        return currentFilePath;
    }

    public void setCurrentFilePath(String currentFilePath) {
        this.currentFilePath = currentFilePath;
    }

    public void setDataTransferSocket(Socket dataTransferSocket) {
        this.dataTransferSocket = dataTransferSocket;
    }
}
