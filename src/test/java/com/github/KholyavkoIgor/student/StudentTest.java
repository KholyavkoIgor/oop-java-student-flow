package com.github.KholyavkoIgor.student;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @Nested
    @DisplayName("Задание 1: базовый класс")
    class Basic {

        @Test
        void createWithNameOnly() {
            Student<Integer> s = new Student<>("Андрей");
            assertEquals("Андрей", s.getName());
            assertTrue(s.getGrades().isEmpty());
        }

        @Test
        void createWithNameAndGrades() {
            Student<Integer> s = new Student<>("Борис", List.of(5, 4, 5));
            assertEquals(List.of(5, 4, 5), s.getGrades());
        }

        @Test
        void constructorCopiesGradesList() {
            List<Integer> source = new ArrayList<>(List.of(1, 2));
            Student<Integer> s = new Student<>("Борис", source);
            source.add(3);
            assertEquals(List.of(1, 2), s.getGrades());
        }

        @Test
        void setNameChangesName() {
            Student<Integer> s = new Student<>("Андрей");
            s.setName("Алексей");
            assertEquals("Алексей", s.getName());
        }

        @Test
        void addGradeAppends() {
            Student<Integer> s = new Student<>("Андрей");
            s.addGrade(3);
            s.addGrade(4);
            assertEquals(List.of(3, 4), s.getGrades());
        }

        @Test
        void removeGradeRemovesFirstOccurrence() {
            Student<Integer> s = new Student<>("Андрей", List.of(5, 3, 5));
            s.removeGrade(5);
            assertEquals(List.of(3, 5), s.getGrades());
        }

        @Test
        void removeMissingGradeDoesNothing() {
            Student<Integer> s = new Student<>("Андрей", List.of(5));
            s.removeGrade(2);
            assertEquals(List.of(5), s.getGrades());
        }

        @Test
        void getGradesReturnsCopy() {
            Student<Integer> s = new Student<>("Борис", List.of(5));
            s.getGrades().add(100);
            assertEquals(List.of(5), s.getGrades());
        }

        @Test
        void toStringFormat() {
            Student<Integer> s = new Student<>("Андрей", List.of(4, 5));
            assertEquals("Андрей: [4, 5]", s.toString());
        }

        @Test
        void toStringWithoutGrades() {
            assertEquals("Андрей: []", new Student<Integer>("Андрей").toString());
        }
    }

    @Nested
    @DisplayName("Задание 1: equals и hashCode")
    class Equality {

        @Test
        void equalWhenSameGradesDifferentOrder() {
            Student<Integer> a = new Student<>("Виктор", List.of(1, 2, 3));
            Student<Integer> b = new Student<>("Виктор", List.of(3, 2, 1));
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
        }

        @Test
        void notEqualWhenDifferentMultiplicity() {
            Student<Integer> a = new Student<>("Галина", List.of(1, 2, 3));
            Student<Integer> b = new Student<>("Галина", List.of(3, 3, 3));
            assertNotEquals(a, b);
        }

        @Test
        void notEqualWhenDifferentNames() {
            assertNotEquals(new Student<>("А", List.of(1)), new Student<>("Б", List.of(1)));
        }

        @Test
        void notEqualWhenDifferentSizes() {
            assertNotEquals(new Student<>("А", List.of(1, 1)), new Student<>("А", List.of(1)));
        }

        @Test
        void reflexive() {
            Student<Integer> a = new Student<>("А", List.of(1));
            assertEquals(a, a);
        }

        @Test
        void symmetricAndTransitive() {
            Student<Integer> a = new Student<>("А", List.of(1, 2));
            Student<Integer> b = new Student<>("А", List.of(2, 1));
            Student<Integer> c = new Student<>("А", List.of(1, 2));
            assertEquals(a, b);
            assertEquals(b, a);
            assertEquals(b, c);
            assertEquals(a, c);
        }

        @Test
        void notEqualToNullOrOtherType() {
            Student<Integer> a = new Student<>("А");
            assertNotEquals(null, a);
            assertNotEquals("А: []", a);
        }

        @Test
        void consistentAfterModification() {
            Student<Integer> a = new Student<>("А", List.of(1));
            Student<Integer> b = new Student<>("А", List.of(1));
            b.addGrade(2);
            assertNotEquals(a, b);
            a.addGrade(2);
            assertEquals(a, b);
        }
    }

    @Nested
    @DisplayName("Задание 2: условия")
    class Validation {

        @Test
        void nullNameRejected() {
            assertThrows(IllegalArgumentException.class, () -> new Student<Integer>(null));
        }

        @Test
        void emptyNameRejected() {
            assertThrows(IllegalArgumentException.class, () -> new Student<Integer>(""));
        }

        @Test
        void setNameRejectsNullAndEmpty() {
            Student<Integer> s = new Student<>("Андрей");
            assertThrows(IllegalArgumentException.class, () -> s.setName(null));
            assertThrows(IllegalArgumentException.class, () -> s.setName(""));
            assertEquals("Андрей", s.getName());
        }

        @Test
        void stringGradesWithRule() {
            Predicate<String> rule = g -> g.equals("зачет") || g.equals("незачет");
            Student<String> s = new Student<>("Егор", rule);
            s.addGrade("зачет");
            s.addGrade("незачет");
            assertEquals(List.of("зачет", "незачет"), s.getGrades());
            assertThrows(InvalidGradeException.class, () -> s.addGrade("отлично"));
            assertEquals(List.of("зачет", "незачет"), s.getGrades());
        }

        @Test
        void invalidGradeIsUnchecked() {
            assertTrue(RuntimeException.class.isAssignableFrom(InvalidGradeException.class));
        }

        @Test
        void invalidGradeInConstructorListRejected() {
            Predicate<Integer> evenOnly = x -> x % 2 == 0;
            assertThrows(InvalidGradeException.class,
                    () -> new Student<>("Дмитрий", List.of(2, 3), evenOnly));
        }

        @Test
        void rangeRule() {
            Predicate<Integer> rule = x -> x >= 1 && x <= 1_000_000 && x % 2 == 0;
            Student<Integer> s = new Student<>("Дмитрий", rule);
            s.addGrade(2);
            assertThrows(InvalidGradeException.class, () -> s.addGrade(0));
            assertThrows(InvalidGradeException.class, () -> s.addGrade(1_000_002));
            assertEquals(List.of(2), s.getGrades());
        }

        @Test
        void noRuleAcceptsAnyValue() {
            Student<Integer> s = new Student<>("Андрей");
            s.addGrade(-100);
            s.addGrade(Integer.MAX_VALUE);
            assertEquals(2, s.getGrades().size());
        }
    }

    @Nested
    @DisplayName("Задание 3: отмена")
    class Undo {

        @Test
        void undoWithoutActionsThrows() {
            Student<Integer> s = new Student<>("Андрей", List.of(1, 2));
            assertThrows(IllegalStateException.class, s::undo);
        }

        @Test
        void undoAddGrade() {
            Student<Integer> s = new Student<>("Андрей", List.of(4, 5));
            s.addGrade(3);
            s.undo();
            assertEquals(List.of(4, 5), s.getGrades());
        }

        @Test
        void undoRemoveGradeRestoresPosition() {
            Student<Integer> s = new Student<>("Андрей", List.of(1, 2, 3));
            s.removeGrade(2);
            s.undo();
            assertEquals(List.of(1, 2, 3), s.getGrades());
        }

        @Test
        void undoSetName() {
            Student<Integer> s = new Student<>("Андрей");
            s.setName("Артём");
            s.undo();
            assertEquals("Андрей", s.getName());
        }

        @Test
        void undoAllActionsInReverseOrderToInitialState() {
            Student<Integer> s = new Student<>("Андрей", List.of(5));
            Student<Integer> initial = new Student<>("Андрей", List.of(5));

            s.addGrade(4);
            s.setName("Артём");
            s.removeGrade(5);
            s.addGrade(3);

            s.undo();
            assertEquals(List.of(4), s.getGrades());
            s.undo();
            assertEquals(List.of(5, 4), s.getGrades());
            s.undo();
            assertEquals("Андрей", s.getName());
            s.undo();
            assertEquals(initial, s);
            assertThrows(IllegalStateException.class, s::undo);
        }

        @Test
        void failedAddGradeNotRecorded() {
            Student<Integer> s = new Student<>("Дмитрий", x -> x % 2 == 0);
            s.addGrade(2);
            assertThrows(InvalidGradeException.class, () -> s.addGrade(3));
            s.undo();
            assertTrue(s.getGrades().isEmpty());
            assertThrows(IllegalStateException.class, s::undo);
        }

        @Test
        void failedSetNameNotRecorded() {
            Student<Integer> s = new Student<>("Андрей");
            s.setName("Артём");
            assertThrows(IllegalArgumentException.class, () -> s.setName(""));
            s.undo();
            assertEquals("Андрей", s.getName());
            assertThrows(IllegalStateException.class, s::undo);
        }

        @Test
        void removeMissingGradeNotRecorded() {
            Student<Integer> s = new Student<>("Андрей", List.of(1));
            s.removeGrade(42);
            assertThrows(IllegalStateException.class, s::undo);
        }
    }
}
