package com.github.KholyavkoIgor.student;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
class FlowTest {

    @Nested
    @DisplayName("Фабричные методы")
    class Factories {

        @Test
        void ofVarargs() {
            List<Integer> result = Flow.of(1, 2, 3).collect(ArrayList::new, List::add);
            assertEquals(List.of(1, 2, 3), result);
        }

        @Test
        void ofVarargsAllowsNull() {
            List<String> result = Flow.of("a", null, "b").collect(ArrayList::new, List::add);
            assertEquals(Arrays.asList("a", null, "b"), result);
        }

        @Test
        void ofList() {
            List<String> result = Flow.of(List.of("a", "b")).collect(ArrayList::new, List::add);
            assertEquals(List.of("a", "b"), result);
        }

        @Test
        void ofListIsIndependentOfSource() {
            List<Integer> source = new ArrayList<>(List.of(1, 2));
            Flow<Integer> flow = Flow.of(source);
            source.add(3);
            assertEquals(3, flow.reduce(Integer::sum));
        }

        @Test
        void iterate() {
            List<Integer> result = Flow.iterate(1, x -> x * 2, x -> x < 20)
                    .collect(ArrayList::new, List::add);
            assertEquals(List.of(1, 2, 4, 8, 16), result);
        }

        @Test
        void iterateWithFalseConditionIsEmpty() {
            List<Integer> result = Flow.iterate(100, x -> x + 1, x -> x < 10)
                    .collect(ArrayList::new, List::add);
            assertTrue(result.isEmpty());
        }

        @Test
        void iterateWithStrings() {
            String result = Flow.iterate("a", s -> s + "a", s -> s.length() <= 3)
                    .reduce((x, y) -> x + "," + y);
            assertEquals("a,aa,aaa", result);
        }
    }

    @Nested
    @DisplayName("map и filter")
    class Intermediate {

        @Test
        void exampleFromTask() {
            assertEquals(4, Flow.of(1, -2, 3).filter(x -> x > 0).reduce((x, y) -> x + y));
        }

        @Test
        void mapSameType() {
            List<Integer> result = Flow.of(1, 2, 3).map(x -> x * x).collect(ArrayList::new, List::add);
            assertEquals(List.of(1, 4, 9), result);
        }

        @Test
        void mapChangesType() {
            List<String> result = Flow.of(1, 22, 333)
                    .map(String::valueOf)
                    .map(s -> s + "!")
                    .collect(ArrayList::new, List::add);
            assertEquals(List.of("1!", "22!", "333!"), result);
        }

        @Test
        void filterAndMapApplyInOrder() {
            // сначала фильтр по исходным значениям, потом преобразование
            List<Integer> a = Flow.of(1, 2, 3, 4).filter(x -> x % 2 == 0).map(x -> x + 1)
                    .collect(ArrayList::new, List::add);
            // сначала преобразование, потом фильтр по новым значениям
            List<Integer> b = Flow.of(1, 2, 3, 4).map(x -> x + 1).filter(x -> x % 2 == 0)
                    .collect(ArrayList::new, List::add);
            assertEquals(List.of(3, 5), a);
            assertEquals(List.of(2, 4), b);
        }

        @Test
        void filterAcceptsSuperTypePredicate() {
            Predicate<Number> positive = n -> n.doubleValue() > 0;
            assertEquals(4, Flow.of(1, -2, 3).filter(positive).reduce(Integer::sum));
        }

        @Test
        void intermediateOperationsReturnSameObject() {
            Flow<Integer> flow = Flow.of(1, 2);
            assertSame(flow, flow.filter(x -> true));
            assertSame(flow, flow.map(x -> x));
        }

        @Test
        void intermediateOperationsAreLazy() {
            AtomicInteger calls = new AtomicInteger();
            Flow<Integer> flow = Flow.of(1, 2, 3)
                    .map(x -> { calls.incrementAndGet(); return x; })
                    .filter(x -> { calls.incrementAndGet(); return true; });
            assertEquals(0, calls.get());
            flow.reduce(Integer::sum);
            assertEquals(6, calls.get());
        }

        @Test
        void iterateIsLazy() {
            AtomicInteger calls = new AtomicInteger();
            Flow<Integer> flow = Flow.iterate(0, x -> { calls.incrementAndGet(); return x + 1; }, x -> x < 5);
            assertEquals(0, calls.get());
            assertEquals(10, flow.reduce(Integer::sum));
        }

        @Test
        void filteredElementSkipsRemainingOperations() {
            AtomicInteger mapCalls = new AtomicInteger();
            Flow.of(1, 2, 3, 4)
                    .filter(x -> x > 2)
                    .map(x -> { mapCalls.incrementAndGet(); return x; })
                    .collect(ArrayList::new, List::add);
            assertEquals(2, mapCalls.get());
        }
    }

    @Nested
    @DisplayName("reduce и collect")
    class Terminal {

        @Test
        void reduceSingleElement() {
            assertEquals(42, Flow.of(42).reduce(Integer::sum));
        }

        @Test
        void reduceIsLeftToRight() {
            assertEquals("abc", Flow.of("a", "b", "c").reduce((x, y) -> x + y));
            assertEquals(-4, Flow.of(1, 2, 3).reduce((x, y) -> x - y)); // (1-2)-3
        }

        @Test
        void reduceEmptyThrows() {
            assertThrows(IllegalStateException.class,
                    () -> Flow.of(new ArrayList<Integer>()).reduce(Integer::sum));
        }

        @Test
        void reduceAllFilteredThrows() {
            assertThrows(IllegalStateException.class,
                    () -> Flow.of(1, 2).filter(x -> x > 10).reduce(Integer::sum));
        }

        @Test
        void collectToSet() {
            Set<Integer> result = Flow.of(1, 2, 2, 3, 3, 3).collect(HashSet::new, Set::add);
            assertEquals(Set.of(1, 2, 3), result);
        }

        @Test
        void collectToStringBuilder() {
            String result = Flow.of("a", "b", "c")
                    .collect(StringBuilder::new, StringBuilder::append)
                    .toString();
            assertEquals("abc", result);
        }

        @Test
        void collectEmptyReturnsEmptyContainer() {
            List<Integer> result = Flow.of(1, 2).filter(x -> false).collect(ArrayList::new, List::add);
            assertTrue(result.isEmpty());
        }

        @Test
        void terminalOperationsAreRepeatable() {
            Flow<Integer> flow = Flow.of(1, 2, 3).map(x -> x * 10);
            assertEquals(60, flow.reduce(Integer::sum));
            assertEquals(60, flow.reduce(Integer::sum));

            Flow<Integer> generated = Flow.iterate(1, x -> x + 1, x -> x <= 3);
            assertEquals(6, generated.reduce(Integer::sum));
            assertEquals(6, generated.reduce(Integer::sum));
        }
    }
}
