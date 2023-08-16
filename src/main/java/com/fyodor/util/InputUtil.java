package com.fyodor.util;

import com.fyodor.exception.OutOfMenuRangeException;
import com.fyodor.util.log.Logger;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * @author Fyodor
 * @version 0.0.1
 *
 * Note: 1)Можно добавить очистку консоли через определение среды исполнения и нативного 'cls' или 'clear'
 *       2)Так же можно добавить скрытый ввод пароля через звездочки '***'
 *
 */
public class InputUtil {
    private static final Scanner scanner = new Scanner(System.in);
    public static String readIpAddress() {
        try {
            return scanner.nextLine().trim();
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }
    public static String readLogin() {
        try {
            return scanner.nextLine().trim();
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    public static String readPassword() {
        try {
            return scanner.nextLine().trim();
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }
    public static String readFilePath() {
        System.out.println("Введите путь до файла: ");
        return scanner.nextLine().trim();
    }
    public static int getUserChoice(int menuHeight) {
        int choiceIndex = -1;
        try {
            System.out.print("Выберите пункт: ");
            choiceIndex = scanner.nextInt();
            if (choiceIndex < 1 || choiceIndex > menuHeight) {
                throw new OutOfMenuRangeException("Выход за границы пункта меню");
            }
        } catch (InputMismatchException e) {
            System.out.println("Ошибка: Введите корректное число.");
            Logger.logWarning("Ошибка выбора пункта меню");
        }
        catch (OutOfMenuRangeException e) {
            System.out.println("Ошибка: Неверный ввод");
            Logger.logWarning("Ошибка выбора пункта меню");
        }
        scanner.nextLine(); // Очистка буфера
        return choiceIndex;
    }

    public static int readStudentId() {
        System.out.println("Введите id студента: ");
        int id = -1;
        try {
            id = scanner.nextInt();
            return id;
        }
        catch (InputMismatchException e) {
            System.out.println("Ошибка: Введите корректное число.");
            Logger.logWarning("Ошибка ввода id");
        }
        catch (Exception e) {
            System.out.println("Ошибка: Неверный ввод");
            Logger.logWarning("Ошибка ввода id");
        }
        scanner.nextLine();
        return id;
    }
    public static String readStudentName() {
        System.out.println("Введите имя студента: ");
        return scanner.nextLine().trim();
    }

    public static void closeResources() {
        scanner.close();
    }
}
