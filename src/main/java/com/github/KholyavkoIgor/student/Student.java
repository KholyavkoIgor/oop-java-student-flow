package com.github.KholyavkoIgor.student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Студент с именем и списком оценок произвольного типа {@code T}.
 * <p>
 * Студенты равны, если равны имена и совпадают списки оценок
 * (кратность оценок учитывается, порядок - нет).
 * При создании объекта Студент ему может быть задано указание на то, что является корректной оценкой, а что нет.
 * Если признак корректности не задан, значит все значения данного типа данных являются корректными оценками.
 * Условие корректности не может быть изменено в течении жизни объекта.
 */

public class Student<T> {
    /**
     * Поля класса Student.
     * @param name имя студента
     * @param grades список оценок студента
     * @param validityRule правило корректности
     */
    private String name;
    private final List<T> grades;
    private final Predicate<T> validityRule;

    //constructors

    /**
     * Создаёт студента с именем и пустым списком оценок.
     * Правило корректности не задано, допустимы любые значения типа {@code T}.
     * @param name имя студента, не может быть {@code null} или пустой строкой
     */
    public Student(String name) {
        this(name, List.of(), t -> true);
    }

    /**
     * Создаёт студента с именем и правилом корректности оценки.
     * @param name имя студента, не может быть {@code null} или пустой строкой
     * @param validityRule правило, определяющее корректность оценки
     */
    public Student(String name, Predicate<T> validityRule){
        this(name, List.of(), validityRule);
    }

    /**
     * Создаёт студента с именем и начальным списком оценок.
     * Правило корректности не задано, допустимы любые значения типа {@code T}.
     * @param name имя студента, не может быть {@code null} или пустой строкой
     * @param grades список оценок студента
     */
    public Student(String name, List<T> grades) {
        this(name, grades, t -> true);
    }

    /**
     * Создаёт студента с именем, начальным списком оценок и правилом корректности оценки.
     *
     * @param name имя студента, не может быть {@code null} или пустой строкой
     * @param grades список оценок студента
     * @param validityRule правило корректности
     * @throws IllegalArgumentException если имя {@code null} или пустое
     */
    public Student(String name, List<T> grades, Predicate<T> validityRule) {
        setName(name);
        this.grades = new ArrayList<>();
        this.validityRule = validityRule;
        for (T grade : grades) {
            addGrade(grade);
        }
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
    public List<T> getGrades(){
        return new ArrayList<>(grades);
    }
    //Setters

    /**
     * Меняет имя студента на новое.
     * @param nameNew новое имя студента, не может быть {@code null} или пустой строкой
     * @throws IllegalArgumentException если имя {@code null} или пустое
     */
    public void setName(String nameNew){
        if(nameNew == null||nameNew.isEmpty()){
            throw new IllegalArgumentException("[!] Name is empty or null");
        }
        this.name = nameNew;
    }

    /**
     * добавлет студенту оценку в список оценок.
     * @param grade добавляемая оценка
     * @throws InvalidGradeException если оценка не проходит правило корректности
     */
    public void addGrade(T grade){
        if(!validityRule.test(grade)){
            throw new InvalidGradeException(grade);
        }
        grades.add(grade);
    }

    /**
     * Удаляет из списка оценок студента первое вхождение оценки с заданным значением.
     * @param grade заданное значение оценки на удаление
     */
    public void removeGrade(T grade){
        grades.remove(grade);
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
        Student<?> other = (Student<?>) o;

        List<?> thisSorted = new ArrayList<>(this.grades);
        List<?> otherSorted = new ArrayList<>(other.grades);
        thisSorted.sort(null);
        otherSorted.sort(null);

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
        List<T> sorted = new ArrayList<>(grades);
        sorted.sort(null);
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
