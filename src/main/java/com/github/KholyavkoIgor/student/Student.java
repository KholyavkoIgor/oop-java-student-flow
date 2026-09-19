package com.github.KholyavkoIgor.student;

import java.util.ArrayList;
import java.util.List;

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

}
