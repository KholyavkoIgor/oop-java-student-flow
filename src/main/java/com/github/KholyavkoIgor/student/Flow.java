package com.github.KholyavkoIgor.student;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.function.Predicate;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;

/**
 * Поток значений с поддержкой промежуточных ({@link #map(Function)}, {@link #filter(Predicate)})
 * и терминальных ({@link #reduce(BinaryOperator)}, {@link #collect(Supplier, BiConsumer)}) операций.
 *<p>
 * Промежуточные операции не выполняются немедленно. Вместо этого они запоминаются и применяются
 * за один проход (в ленивом режиме) в момент вызова терминальной операции.
 * <p>
 * Методы {@link #map(Function)} и {@link #filter(Predicate)} изменяют текущий объект и возвращают его же.
 * Терминальные операции не расходуют поток и могут вызываться повторно.
 * @param <T> тип элементов потока
 */
public class Flow<T> {
    private static final Object SKIP = new Object();
    private final Iterable<T> source;
    private final List<Function<Object,Object>> operations = new ArrayList<>();

    private Flow(Iterable<T> source){
        this.source=source;
    }
    /**
     * Создает поток из элементов указанного списка.
     * @param values список значений
     * @param <T>    тип элементов
     * @return новый экземпляр {@code Flow}
     */
    public static <T> Flow<T> of(List<T> values) {
        return new Flow<>(new ArrayList<>(values));
    }
    /**
     * Создает поток из элементов, переданных в виде переменного числа аргументов.
     * @param values массив значений
     * @param <T>    тип элементов
     * @return новый экземпляр {@code Flow}
     */
    @SafeVarargs
    public static <T> Flow<T> of(T... values) {
        return new Flow<>(new ArrayList<>(Arrays.asList(values)));
    }
    /**
     * Создает поток, генерирующий значения на основе начального элемента (seed),
     * функции перехода к следующему элементу и условия продолжения.
     * <p>
     * Генерация продолжается до тех пор, пока {@code condition} возвращает {@code true}.
     * @param seed      начальное значение
     * @param nextStep  функция для вычисления следующего элемента
     * @param condition условие, при котором генерация продолжается
     * @param <T>       тип элементов
     * @return новый экземпляр {@code Flow}
     */
    public static <T> Flow<T> iterate(T seed, UnaryOperator<T> nextStep, Predicate<? super T> condition){
        return new Flow<>(() -> new Iterator<T>() {
            private T current = seed;
            @Override
            public boolean hasNext() {
                return condition.test(current);
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T result = current;
                current = nextStep.apply(current);
                return result;
            }
        });
    }
    /**
     * Добавляет операцию преобразования элементов потока.
     * <p>
     * Это промежуточная операция: она не выполняется немедленно, а запоминается
     * для последующего применения в терминальных операциях.
     * @param fn   функция преобразования
     * @param <R>  тип элементов результирующего потока
     * @return поток с примененной операцией преобразования (фактически возвращает {@code this})
     */
    @SuppressWarnings("unchecked")
    public <R> Flow<R> map(Function<? super T, ? extends R> fn) {
        operations.add(value -> fn.apply((T) value));
        return (Flow<R>) (Flow<?>) this;
    }
    /**
     * Добавляет операцию фильтрации элементов потока.
     * <p>
     * Элементы, для которых предикат возвращает {@code false}, будут помечены как пропущенные
     * и не попадут в результат терминальных операций.
     * @param predicate предикат, определяющий, должен ли элемент остаться в потоке
     * @return поток с примененной операцией фильтрации (фактически возвращает {@code this})
     */
    @SuppressWarnings("unchecked")
    public Flow<T> filter(Predicate<? super T> predicate){
        operations.add(value->predicate.test((T) value)?value:SKIP);
        return this;
    }
    /**
     * Применяет все запомненные промежуточные операции к одному значению.
     * @param value исходное значение
     * @return преобразованное значение или {@link #SKIP}, если элемент был отфильтрован
     */
    private Object applyOperations(Object value) {
        for (Function<Object, Object> op : operations) {
            value = op.apply(value);
            if (value == SKIP) {
                return SKIP;
            }
        }
        return value;
    }
    /**
     * Выполняет терминальную операцию свертывания (редукции) потока в одно значение.
     * <p>
     * Применяет бинарную операцию к накопителю и каждому элементу потока (слева направо)
     * для получения единого результата.
     * @param operator бинарная операция для объединения элементов
     * @return результирующее значение
     * @throws IllegalStateException если поток пуст (не содержит элементов после применения фильтров)
     */
    @SuppressWarnings("unchecked")
    public T reduce(BinaryOperator<T> operator) {
        T accumulator = null;
        boolean initialized = false;

        for (T rawValue : source) {
            Object processed = applyOperations(rawValue);
            if (processed == SKIP) {
                continue;
            }
            T value = (T) processed;
            if (!initialized) {
                accumulator = value;
                initialized = true;
            } else {
                accumulator = operator.apply(accumulator, value);
            }
        }

        if (!initialized) {
            throw new IllegalStateException("Нельзя вызвать reduce на пустом потоке");
        }
        return accumulator;
    }
    /**
     * Выполняет терминальную операцию сбора элементов потока в пользовательский контейнер.
     * @param containerFactory поставщик (фабрика) для создания пустого контейнера
     * @param accumulator      функция, добавляющая элемент потока в контейнер
     * @param <C>              тип контейнера
     * @return заполненный контейнер
     */
    @SuppressWarnings("unchecked")
    public <C> C collect(Supplier<C> containerFactory, BiConsumer<C, ? super T> accumulator) {
        C container = containerFactory.get();
        for (T rawValue : source) {
            Object processed = applyOperations(rawValue);
            if (processed == SKIP) {
                continue;
            }
            accumulator.accept(container, (T) processed);
        }
        return container;
    }


}
