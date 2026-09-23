package com.github.KholyavkoIgor.student;

import java.util.List;

/**
 * Точка входа приложения. Демонстрация примеров работы с классом {@link Student}.
 */
public class Main {
    public static void main(String[] args){
        // имя + нет оценок
        Student stewdent1 = new Student("Андрей");
        stewdent1.addGrade(3);
        stewdent1.addGrade(4);
        stewdent1.addGrade(5);
        System.out.println("Студент 1: "+ stewdent1);

        // имя + список оценок
        Student stewdent2 = new Student("Борис", List.of(5, 4, 5));
        System.out.println("Студент 2: " + stewdent2);
        stewdent1.setName("Алексей");
        System.out.println("Смена имени: " + stewdent1);

        //изменение снаружи не влияет на студента
        List<Integer> gradesCopy = stewdent2.getGrades();
        gradesCopy.add(100);
        System.out.println("Оценки студента 2 после попытки изменить копию извне: "
                + stewdent2.getGrades());

        // Удаление оценки
        stewdent1.removeGrade(3);
        System.out.println("После удаления оценки 3: " + stewdent1);

        // Сравнение студентов: одинаковые оценки, но разный порядок
        Student stewdent3 = new Student("Виктор", List.of(1, 2, 3));
        Student stewdent4 = new Student("Виктор", List.of(3, 2, 1));
        System.out.println("stewdent3 равен stewdent4 : "
                + stewdent3.equals(stewdent4));
        // Сравнение студентов: одинаковый набор чисел, но разная кратность
        Student stewdent5 = new Student("Галина", List.of(1, 2, 3));
        Student stewdent6 = new Student("Галина", List.of(3, 3, 3));
        System.out.println("s5 равен s6 (разная кратность): " + stewdent5.equals(stewdent6));



    }
}
