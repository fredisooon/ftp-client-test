package com.fyodor.menu;


import com.fyodor.App;
import com.fyodor.util.InputUtil;

public class StartMenu implements Menu {

    @Override
    public void displayMenu() {
        System.out.println("\n===== FTP CLIENT CLI =====");
        System.out.println("1. Подключиться к FTP серверу");
        System.out.println("2. Настройки");
        System.out.println("3. Завершение работы");
        processUserChoice();
    }

    @Override
    public void processUserChoice() {
        int choice = InputUtil.getUserChoice(3);
        switch (choice) {
            case 1:
                FtpConnectionMenu ftpConnectionMenu = new FtpConnectionMenu();
                ftpConnectionMenu.displayMenu();
                break;
            case 2:
                SettingsMenu settingsMenu = new SettingsMenu();
                settingsMenu.displayMenu();
                break;
            case 3:
                System.out.println("Выход...");
                App.exit();
                break;
        }
    }
}
