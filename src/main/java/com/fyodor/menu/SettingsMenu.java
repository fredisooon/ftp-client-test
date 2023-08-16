package com.fyodor.menu;

import com.fyodor.util.InputUtil;
import com.fyodor.util.log.Logger;

public class SettingsMenu implements Menu {
    private final int menuHeight = 2;
    public static boolean backToPrevious;
    @Override
    public void displayMenu() {
        backToPrevious = false;
        while (!backToPrevious) {
            System.out.println("\n===== SETTINGS =====");
            System.out.println("1. " + currentState() + " логирование");
            System.out.println("2. Назад");
            processUserChoice();
        }
    }

    @Override
    public void processUserChoice() {
        int choice = InputUtil.getUserChoice(menuHeight);
        switch (choice) {
            case 1:
                Logger.setLoggingEnabled();
                break;
            case 2:
                backToPrevious = true;
                break;
        }
    }
    private String currentState() {
        if (Logger.getLoggingEnabled())
            return "Выключить";
        else
            return "Включить";
    }
}
