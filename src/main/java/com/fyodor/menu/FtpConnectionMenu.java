package com.fyodor.menu;

import com.fyodor.ftp.FtpClient;
import com.fyodor.menu.Menu;
import com.fyodor.menu.ServerMenu;
import com.fyodor.util.InputUtil;

public class FtpConnectionMenu implements Menu {
    @Override
    public void displayMenu() {
        System.out.println("\n===== CONNECTING TO FTP-SERVER =====");
        System.out.println("Введите IP-адрес FTP-сервера: ");
        String serverIp = InputUtil.readIpAddress();
        System.out.println("Введите логин: ");
        String username = InputUtil.readLogin();
        System.out.println("Введите пароль: ");
        String password = InputUtil.readPassword();

        FtpClient ftpClient = FtpClient.getInstance();
        ftpClient.connect(serverIp, username, password);
//        ftpClient.connect(
//                "ftp.dlptest.com",
//                "dlpuser",
//                "rNrKYTX9g7z3RgJRmxWuGHbeu"
//        );
        if (ftpClient.isConnected()) {
            ServerMenu menuUtil = new ServerMenu(FtpClient.getInstance());
            menuUtil.displayMenu();
        }
    }
    @Override
    public void processUserChoice() {

    }
}
