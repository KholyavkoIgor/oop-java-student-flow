package com.github.KholyavkoIgor.student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Студент как класс.
 * <p>
 * Студенты равны, если равны имена и совпадают списки оценок
 * (кратность оценок учитывается, порядок - нет).
 */
public class Student {
    /**
     * Поля класса Student.
     * @param name - имя студента
     * @param grades - список оценок студента
     */
    private String name;
    private List<Integer> grades;
    //constructors

    /**
     * Создаёт студента с именем и пустым списком оценок.
     * @param name
     */
    public Student(String name){
        this.name = name;
        this.grades = new ArrayList<>();
    }

    /**
     * Создаёт студента с именем и начальным списком оценок.
     * При передаче список копируется, чтобы изменения исходного списка не изменяли состояния студента.
     * @param name
     * @param grades
     */
    public Student(String name,List<Integer> grades){
        this.name = name;
        this.grades = new ArrayList<>(grades);
    }
    //Getters:

    /**
     * Возвращает имя студента.
     * @return имя студента
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает копию текущего списка оценок студента.
     * @return копия списка оценок
     */
    public List<Integer> getGrades(){
        return new ArrayList<>(grades);
    }
    //Setters

    /**
     * Меняет имя студента на новое.
     * @param nameNew новое имя студента
     */
    public void setName(String nameNew){
        this.name = nameNew;
    }

    /**
     * добавлет студенту оценку в список оценок.
     * @param grade добавляемая оценка
     */
    public void addGrade(int grade){
        grades.add(grade);
    }

    /**
     * Удаляет из списка оценок студента первое вхождение оценки с заданным значением.
     * @param grade заданное значение оценки на удаление
     */
    public void removeGrade(int grade){
        grades.remove(Integer.valueOf(grade));
    }

    /**
     * Производит сравнение студента и объекта.
     * Студенты равны, если равны имена и совпадают списки оценок (кратность учитывается, порядок - нет)
     *
     * @param o   объект для сравнения
     * @return {@code true}, если объекты равны
     */
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student other = (Student) o;

        List<Integer> thisSorted = new ArrayList<>(this.grades);
        List<Integer> otherSorted = new ArrayList<>(other.grades);
        Collections.sort(thisSorted);
        Collections.sort(otherSorted);

        return Objects.equals(this.name, other.name)
                && thisSorted.equals(otherSorted);
    }

    /**
     * Возвращает хэш-код, согласованный с {@link #equals(Object)}:
     * Порядок оценок в списке не влияет.
     *
     * @return хэш-код студента
     */
    @Override
    public int hashCode(){
        List<Integer> sorted = new ArrayList<>(grades);
        Collections.sort(sorted);
        return Objects.hash(name, sorted);
    }

    /**
     * Возвращает строковое представление студента в формате
     * {@code "Имя: [оценка0, оценка1, оценка2, ..., оценкаN-1]"}.
     * @return строковое представление студента
     */
    @Override
    public String toString(){
        return name + ": " + grades.toString();
    }
}
