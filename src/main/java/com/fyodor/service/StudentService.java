package com.fyodor.service;

import com.fyodor.ftp.FileTransferManager;
import com.fyodor.ftp.FtpClient;
import com.fyodor.menu.CRUDMenu;
import com.fyodor.model.Student;
import com.fyodor.util.InputUtil;
import com.fyodor.util.log.Logger;

import javax.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class StudentService {
    private List<Student> students;

    private JsonObject readJsonObjectFromFile() {
        try {
            InputStream inputStream = new ByteArrayInputStream(FtpClient.getInstance().getFileBuffer().toByteArray());
            return Json.createReader(inputStream).readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Student> parseStudentsFromJson(JsonArray studentsArray) {
        List<Student> students = new ArrayList<>();
        for (JsonValue studentValue : studentsArray) {
            JsonObject studentObject = (JsonObject) studentValue;
            int id = studentObject.getInt("id");
            String name = studentObject.getString("name");
            students.add(new Student(id, name));
        }

        // Сортировка списка студентов в алфавитном порядке
        if (CRUDMenu.ascOrder) {
            students.sort(Comparator.comparing(Student::getName));
        }
        return students;
    }

    private void writeJsonObjectToFile(JsonObject jsonObject) {
        try {
            byte[] jsonBytes = jsonObject.toString().getBytes(StandardCharsets.UTF_8);

            ByteArrayOutputStream baos = new ByteArrayOutputStream(jsonBytes.length);
            FtpClient.getInstance().setFileBuffer(baos);
            baos.write(jsonBytes, 0, jsonBytes.length);
            FileTransferManager.uploadModifiedFile(baos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean readStudentsListFromFile() {
        JsonObject jsonObject = readJsonObjectFromFile();
        if (jsonObject != null) {
            JsonArray studentsArray = jsonObject.getJsonArray("students");
            students = parseStudentsFromJson(studentsArray);
            return true;
        }
        else return false;
    }

    public void getStudentsByName() {
        String studentName = InputUtil.readStudentName();

        if (readStudentsListFromFile()) {
            List<Student> list = students.stream()
                            .filter(student -> student.getName().equals(studentName))
                                    .collect(Collectors.toList());
            if (list.size() == 0) {
                System.out.println("Студентов с именем " + studentName + " не найдено");
            }
            else {
                System.out.println("Список студентов с именем - " + studentName + ": ");
                for (Student student : list) {
                    System.out.println("ID: " + student.getId() + ", Name: " + student.getName());
                }
            }
        }
        else {
            Logger.logWarning("Ошибка чтения JSON из файла");
            System.out.println("Не удалось обработать файл");
        }
    }

    public void getAllStudents() {
        JsonObject jsonObject = readJsonObjectFromFile();

        if (readStudentsListFromFile()) {
            System.out.println("Список всех студентов:");
            for (Student student : students) {
                System.out.println("ID: " + student.getId() + ", Name: " + student.getName());
            }
        }
        else {
            Logger.logWarning("Ошибка чтения JSON из файла");
            System.out.println("Не удалось обработать файл");
        }
    }

    public void getStudentInfoById() {
        int studentId = InputUtil.readStudentId();

        if (readStudentsListFromFile()) {
            Optional<Student> student = students.stream()
                    .filter(s -> s.getId() == studentId)
                    .findFirst();

            if (student.isPresent()) {
                System.out.println("ID: " + student.get().getId() + ", Name: " + student.get().getName());
            }
            else {
                System.out.println("Студента с ID " + studentId + " не найдено");
            }
        }
        else {
            Logger.logWarning("Ошибка чтения JSON из файла");
            System.out.println("Не удалось обработать файл");
        }
    }

    public void addStudent() {
        String studentName = InputUtil.readStudentName();
        JsonObject jsonObject = readJsonObjectFromFile();

        if (jsonObject != null) {
            JsonArray studentsArray = jsonObject.getJsonArray("students");

            int maxId = 0;
            for (JsonValue studentValue : studentsArray) {
                JsonObject studentObject = (JsonObject) studentValue;
                int id = studentObject.getInt("id");
                if (id > maxId) {
                    maxId = id;
                }
            }

            int newStudentId = maxId + 1;

            JsonObject newStudentObject = Json.createObjectBuilder()
                    .add("id", newStudentId)
                    .add("name", studentName)
                    .build();

            JsonArrayBuilder updatedStudentsArrayBuilder = Json.createArrayBuilder(studentsArray)
                    .add(newStudentObject);

            JsonObject updatedJsonObject = Json.createObjectBuilder(jsonObject)
                    .add("students", updatedStudentsArrayBuilder)
                    .build();

            writeJsonObjectToFile(updatedJsonObject);
            System.out.println("Студент успешно добавлен в файл.");
        }
    }

    public void deleteStudentById() {
        int studentId = InputUtil.readStudentId();
        if (studentId == -1) return;
        JsonObject jsonObject = readJsonObjectFromFile();

        if (jsonObject != null) {
            JsonArray studentsArray = jsonObject.getJsonArray("students");
            if (studentsArray.size() < studentId) {
                System.out.println("Студента с ID " + studentId + " не существует");
                return;
            }
            JsonArrayBuilder updatedStudentsArrayBuilder = Json.createArrayBuilder();

            for (JsonValue studentValue : studentsArray) {
                JsonObject studentObject = (JsonObject) studentValue;
                int id = studentObject.getInt("id");

                if (id != studentId) {
                    if (id > studentId) {
                        studentObject = Json.createObjectBuilder(studentObject)
                                .add("id", id - 1)
                                .build();
                    }
                    updatedStudentsArrayBuilder.add(studentObject);
                }
            }

            JsonObject updatedJsonObject = Json.createObjectBuilder(jsonObject)
                    .add("students", updatedStudentsArrayBuilder)
                    .build();

            writeJsonObjectToFile(updatedJsonObject);
            System.out.println("Студент с ID " + studentId + " успешно удален из файла.");
        }
    }

}
