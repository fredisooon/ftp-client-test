package com.fyodor.menu;


import com.fyodor.App;
import com.fyodor.ftp.FileTransferManager;
import com.fyodor.ftp.FtpClient;
import com.fyodor.util.InfoUtil;
import com.fyodor.util.InputUtil;

public class ServerMenu implements Menu {
    private final int menuHeight = 7;

    private final FtpClient ftpClient;

    public ServerMenu(FtpClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    @Override
    public void displayMenu()  {
        while (ftpClient.isConnected()) {
            System.out.println("\n===== FTP MENU " + InfoUtil.currentHostAndMode() + InfoUtil.currentFile() + " =====");
            System.out.println("1. CRUD операции");
            System.out.println("2. Выбрать режим работы" + hint());
            System.out.println("3. Загрузить файл");
            System.out.println("4. DEV TOOLS");
            System.out.println("5. Настройки");
            System.out.println("6. Отключиться от сервера");
            System.out.println("7. Выход");
            processUserChoice();
        }
    }


    @Override
    public void processUserChoice() {
        int choice = InputUtil.getUserChoice(menuHeight);
        switch (choice) {
            case 1:
                CRUDMenu crudMenu = new CRUDMenu();
                crudMenu.displayMenu();
                break;
            case 2:
                ftpClient.setMode();
                break;
            case 3:
                FileTransferManager.downloadRemoteFile();
                break;
            case 4:
                DevToolMenu devToolMenu = new DevToolMenu();
                devToolMenu.displayMenu();
                break;
            case 5:
                SettingsMenu settingsMenu = new SettingsMenu();
                settingsMenu.displayMenu();
                break;
            case 6:
                ftpClient.disconnect();
                break;
            case 7:
                System.out.println("Выход...");
                App.exit();
                break;
        }
    }



    private String hint() {
        if (ftpClient.getMode() == null)
            return " <-*";
        else
            return "";
    }
}
