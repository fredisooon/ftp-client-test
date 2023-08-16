package com.fyodor.menu;

import com.fyodor.ftp.FileTransferManager;
import com.fyodor.util.InputUtil;

public class DevToolMenu implements Menu {
    private final int menuHeight = 3;
    public static boolean backToPrevious;
    @Override
    public void displayMenu() {
        backToPrevious = false;
        while (!backToPrevious) {
            System.out.println("\n===== DEV MODE TOOLS =====");
            System.out.println("1. Выгрузить список студентов.");
            System.out.println("2. Вывести список файлов на сервере");
            System.out.println("3. Назад");
            processUserChoice();
        }
    }

    @Override
    public void processUserChoice() {
        int choice = InputUtil.getUserChoice(menuHeight);
        switch (choice) {
            case 1:
                FileTransferManager.uploadMockFile("mock-students.json");
                break;
            case 2:
                FileTransferManager.displayRemoteFiles();
                break;
            case 3:
                backToPrevious = true;
                break;
        }
    }
}
