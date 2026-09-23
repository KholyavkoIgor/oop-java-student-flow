package com.github.KholyavkoIgor.student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Student {
    //fields
    private String name;
    private List<Integer> grades;
    //constructors
    public Student(String name){
        this.name = name;
        this.grades = new ArrayList<>();
    }
    public Student(String name,List<Integer> grades){
        this.name = name;
        this.grades = new ArrayList<>(grades);
    }
    //Getters:
    public String getName() {
        return name;
    }
    public List<Integer> getGrades(){
        return new ArrayList<>(grades);
    }
    //Setters
    public void setName(String nameNew){
        this.name = nameNew;
    }
    public void addGrade(int grade){
        grades.add(grade);
    }
    public void removeGrade(int grade){
        grades.remove(Integer.valueOf(grade));
    }
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
    @Override
    public int hashCode(){
        List<Integer> sorted = new ArrayList<>(grades);
        Collections.sort(sorted);
        return Objects.hash(name, sorted);
    }

    @Override
    public String toString(){
        return name + ": " + grades.toString();
    }
}
