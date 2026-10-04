package com.github.KholyavkoIgor.student;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Точка входа приложения. Демонстрация примеров работы с классом {@link Student}.
 */
public class Main {
    public static void main(String[] args){

        // имя + нет оценок
        Student<Integer> stewdent1 = new Student<>("Андрей");
        stewdent1.addGrade(3);
        stewdent1.addGrade(4);
        stewdent1.addGrade(5);
        System.out.println("Студент 1: "+ stewdent1);

        // имя + список оценок
        Student<Integer> stewdent2 = new Student<>("Борис", List.of(5, 4, 5));
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
        Student<Integer> stewdent3 = new Student<>("Виктор", List.of(1, 2, 3));
        Student<Integer> stewdent4 = new Student<>("Виктор", List.of(3, 2, 1));
        System.out.println("stewdent3 равен stewdent4 : "
                + stewdent3.equals(stewdent4));
        // Сравнение студентов: одинаковый набор чисел, но разная кратность
        Student<Integer> stewdent5 = new Student<>("Галина", List.of(1, 2, 3));
        Student<Integer> stewdent6 = new Student<>("Галина", List.of(3, 3, 3));
        System.out.println("stewdent5 равен stewdent6 (разная кратность): " + stewdent5.equals(stewdent6));


        Predicate<Integer> evenOnly = x -> x % 2 == 0;
        Student<Integer> stewdent7 = new Student<>("Дмитрий", evenOnly);
        stewdent7.addGrade(4);
        try {
            stewdent7.addGrade(3);
        }
        catch (InvalidGradeException e){
            System.out.println("Expected error: " + e.getMessage());
        }
        Predicate<String> neDiff = s -> s.equals("зачет") || s.equals("незачет");
        Student<String> stewdent8 = new Student<>("Егор", neDiff);
        stewdent8.addGrade("зачет");
        stewdent8.addGrade("зачет");
        stewdent8.addGrade("незачет");
        System.out.println("stewdent8 не равен stewdent6 (inconvertible types): " + stewdent8.equals(stewdent6));
        System.out.println("Оценки студента " + stewdent8.getName() + ": "
                + stewdent8.getGrades());
        //new Student<>("Аня", null); // ambiguous method call

        Student<Integer> s = new Student<>("Андрей", List.of(4, 5));
        System.out.println(s); // Андрей: [4, 5]
        s.addGrade(3);
        System.out.println(s); // Андрей: [4, 5, 3]

        s.undo();
        System.out.println(s); // Андрей: [4, 5]

        s.setName("Артём");
        System.out.println(s.getName()); //Артём
        s.undo();
        System.out.println(s.getName()); // Андрей
        try {
            s.undo();
        } catch (IllegalStateException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
        System.out.println(Flow.of(1, -2, 3).filter(x -> x > 0).reduce(Integer::sum)); // 4
        
        List<Integer> squares = Flow.of(List.of(1, 2, 3, 4))
                .map(x -> x * x)
                .collect(ArrayList::new, List::add);                                  // [1, 4, 9, 16]
        System.out.println(squares);

        System.out.println(Flow.iterate(1, x -> x * 2, x -> x < 100).reduce(Integer::sum)); // 127

        try {
            Flow.of(1, 2).filter(x -> x > 10).reduce(Integer::sum);
        } catch (IllegalStateException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
    }
}
