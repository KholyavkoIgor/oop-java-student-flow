package com.github.KholyavkoIgor.student;

public class InvalidGradeException extends RuntimeException {
    public InvalidGradeException(Object grade) {
        super("[!] Invalid grade: " + grade);
    }
}