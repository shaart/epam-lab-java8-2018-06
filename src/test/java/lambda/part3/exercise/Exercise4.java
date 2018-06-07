package lambda.part3.exercise;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;
import lambda.data.Employee;
import lambda.data.JobHistoryEntry;
import lambda.data.Person;
import org.junit.Test;

@SuppressWarnings({"unused", "ConstantConditions"})
public class Exercise4 {

    private static class LazyCollectionHelper<T, R> {

        private List<T> source;
        private Queue<R> currentResultQueue;
        private Consumer<T> convert;

        private LazyCollectionHelper(List<T> source, Queue<R> currentResultQueue,
                Consumer<T> consumer) {
            this.source = source;
            this.currentResultQueue = currentResultQueue;
            this.convert = consumer;
        }

        public static <T> LazyCollectionHelper<T, T> from(List<T> list) {
            Queue<T> nextResultQueue = new LinkedList<>();
            return new LazyCollectionHelper<>(list, nextResultQueue, nextResultQueue::offer);
        }

        public <U> LazyCollectionHelper<T, U> flatMap(Function<R, List<U>> flatMapping) {
            Queue<U> nextResultQueue = new LinkedList<>();
            return new LazyCollectionHelper<>(source, nextResultQueue, convert.andThen(r -> {
                while (!currentResultQueue.isEmpty()) {
                    flatMapping.apply(currentResultQueue.poll())
                            .forEach(nextResultQueue::offer);
                }
            }));
        }

        public <U> LazyCollectionHelper<T, U> map(Function<R, U> mapping) {
            Queue<U> nextResultQueue = new LinkedList<>();
            return new LazyCollectionHelper<>(source, nextResultQueue, convert.andThen(r -> {
                while (!currentResultQueue.isEmpty()) {
                    nextResultQueue.offer(mapping.apply(currentResultQueue.poll()));
                }
            }));
        }

        public List<R> force() {
            source.forEach(convert);
            return new ArrayList<>(currentResultQueue);
        }
    }

    @Test
    public void mapEmployeesToCodesOfLetterTheirPositionsUsingLazyFlatMapHelper() {
        List<Employee> employees = getEmployees();

        // List<Integer> codes = LazyCollectionHelper.from(employees)
        //                                  .flatMap(Employee -> JobHistoryEntry)
        //                                  .map(JobHistoryEntry -> String(position))
        //                                  .flatMap(String -> Character(letter))
        //                                  .map(Character -> Integer(code letter)
        //                                  .force();
        List<Integer> codes = LazyCollectionHelper.from(employees)
                .flatMap(Employee::getJobHistory)
                .map(JobHistoryEntry::getPosition)
                .flatMap(Exercise4::extractCharacters)
                .map(Exercise4::extractCode)
                .force();
        assertEquals(calcCodes("dev", "dev", "tester", "dev", "dev", "QA", "QA", "dev", "tester",
                "tester", "QA", "QA", "QA", "dev"), codes);
    }

    private static List<Character> extractCharacters(String string) {
        List<Character> characters = new ArrayList<>();
        for (char character : string.toCharArray()) {
            characters.add(character);
        }

        return characters;
    }

    private static Integer extractCode(Character character) {
        return (int) character;
    }

    private static List<Integer> calcCodes(String... strings) {
        List<Integer> codes = new ArrayList<>();
        for (String string : strings) {
            for (char letter : string.toCharArray()) {
                codes.add((int) letter);
            }
        }
        return codes;
    }

    private static List<Employee> getEmployees() {
        return Arrays.asList(
                new Employee(
                        new Person("Иван", "Мельников", 30),
                        Arrays.asList(
                                new JobHistoryEntry(2, "dev", "EPAM"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("Александр", "Дементьев", 28),
                        Arrays.asList(
                                new JobHistoryEntry(1, "tester", "EPAM"),
                                new JobHistoryEntry(1, "dev", "EPAM"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("Дмитрий", "Осинов", 40),
                        Arrays.asList(
                                new JobHistoryEntry(3, "QA", "yandex"),
                                new JobHistoryEntry(1, "QA", "mail.ru"),
                                new JobHistoryEntry(1, "dev", "mail.ru")
                        )),
                new Employee(
                        new Person("Анна", "Светличная", 21),
                        Collections.singletonList(
                                new JobHistoryEntry(1, "tester", "T-Systems")
                        )),
                new Employee(
                        new Person("Игорь", "Толмачёв", 50),
                        Arrays.asList(
                                new JobHistoryEntry(5, "tester", "EPAM"),
                                new JobHistoryEntry(6, "QA", "EPAM")
                        )),
                new Employee(
                        new Person("Иван", "Александров", 33),
                        Arrays.asList(
                                new JobHistoryEntry(2, "QA", "T-Systems"),
                                new JobHistoryEntry(3, "QA", "EPAM"),
                                new JobHistoryEntry(1, "dev", "EPAM")
                        ))
        );
    }

}
