package com.fyodor.menu;

import com.fyodor.service.StudentService;
import com.fyodor.util.InfoUtil;
import com.fyodor.util.InputUtil;

public class CRUDMenu implements Menu{
    private final int menuHeight = 7;
    public static boolean ascOrder = false;
    public static boolean backToPrevious;
    private final StudentService studentService;
    public CRUDMenu() {
        this.studentService = new StudentService();
    }

    @Override
    public void displayMenu() {
        backToPrevious = false;
        while (!backToPrevious) {
            System.out.println("\n===== FTP CRUD MENU " + InfoUtil.currentHostAndMode() + InfoUtil.currentFile() + InfoUtil.currentSort() + " =====");
            System.out.println("1. Получение списка всех студентов");
            System.out.println("2. Получение списка студентов по имени");
            System.out.println("3. Получение информации о студенте по id");
            System.out.println("4. Добавление студента");
            System.out.println("5. Удаление студента");
            System.out.println("6. " + currentState() + " сортировку по имени");
            System.out.println("7. Назад");
            processUserChoice();
        }
    }

    private String currentState() {
        if (ascOrder)
            return "Выключить";
        else
            return "Включить";
    }

    @Override
    public void processUserChoice() {
        int choice = InputUtil.getUserChoice(menuHeight);
        switch (choice) {
            case 1:
                studentService.getAllStudents();
                break;
            case 2:
                studentService.getStudentsByName();
                break;
            case 3:
                studentService.getStudentInfoById();
                break;
            case 4:
                studentService.addStudent();
                break;
            case 5:
                studentService.deleteStudentById();
                break;
            case 6:
                ascOrder = !ascOrder;
                break;
            case 7:
                backToPrevious = true;
                break;
        }
    }
}
