package streams.part2.exercise;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import lambda.data.Employee;
import lambda.data.JobHistoryEntry;
import lambda.data.Person;
import org.junit.Test;

@SuppressWarnings({"ConstantConditions", "unused"})
public class Exercise1 {

    @Test
    public void calcTotalYearsSpentInEpam() {
        List<Employee> employees = getEmployees();

        // Реализация
        Long hours = employees.stream()
                .map(Employee::getJobHistory)
                .flatMap(Collection::stream)
                .filter(jobHistoryEntry -> "EPAM".equalsIgnoreCase(jobHistoryEntry.getEmployer()))
                .mapToLong(JobHistoryEntry::getDuration)
                .sum();

        assertEquals(19, hours.longValue());
    }

    @Test
    public void findPersonsWithQaExperience() {
        List<Employee> employees = getEmployees();

        // Реализация
        Set<Person> workedAsQa = employees.stream()
                .filter(employee -> employee.getJobHistory().stream()
                        .anyMatch(jobHistoryEntry ->
                                "QA".equalsIgnoreCase(jobHistoryEntry.getPosition())))
                .map(Employee::getPerson)
                .collect(Collectors.toSet());

        assertEquals(new HashSet<>(Arrays.asList(
                employees.get(2).getPerson(),
                employees.get(4).getPerson(),
                employees.get(5).getPerson()
        )), workedAsQa);
    }

    @Test
    public void composeFullNamesOfEmployeesUsingLineSeparatorAsDelimiter() {
        List<Employee> employees = getEmployees();

        // Реализация
        String result = employees.stream()
                .map(Employee::getPerson)
                .map(Person::getFullName)
                .collect(Collectors.joining("\n"));

        assertEquals("Иван Мельников\n"
                + "Александр Дементьев\n"
                + "Дмитрий Осинов\n"
                + "Анна Светличная\n"
                + "Игорь Толмачёв\n"
                + "Иван Александров", result);
    }

    @Test
    @SuppressWarnings("Duplicates")
    public void groupPersonsByFirstPositionUsingToMap() {
        List<Employee> employees = getEmployees();

        // Реализация
        Function<Employee, String> firstPosition = employee -> employee.getJobHistory().stream()
                .map(JobHistoryEntry::getPosition)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        BinaryOperator<Set<Person>> mergeSets = (leftEmployees, rightEmployees) -> {
            leftEmployees.addAll(rightEmployees);
            return leftEmployees;
        };

        Function<Employee, Set<Person>> getEmployeeSet = employee -> new HashSet<>(
                Collections.singletonList(employee.getPerson()));

        Map<String, Set<Person>> result = employees.stream()
                .collect(Collectors.toMap(firstPosition, getEmployeeSet, mergeSets));

        Map<String, Set<Person>> expected = new HashMap<>();
        expected.put("QA", new HashSet<>(
                Arrays.asList(employees.get(2).getPerson(), employees.get(5).getPerson())));
        expected.put("dev", Collections.singleton(employees.get(0).getPerson()));
        expected.put("tester", new HashSet<>(Arrays.asList(
                employees.get(1).getPerson(),
                employees.get(3).getPerson(),
                employees.get(4).getPerson()))
        );
        assertEquals(expected, result);
    }

    @Test
    @SuppressWarnings("Duplicates")
    public void groupPersonsByFirstPositionUsingGroupingByCollector() {
        List<Employee> employees = getEmployees();

        // Реализация
        Function<Employee, String> firstPosition = employee -> employee.getJobHistory().stream()
                .map(JobHistoryEntry::getPosition)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        Map<String, Set<Person>> result = employees.stream()
                .collect(Collectors.groupingBy(firstPosition,
                        Collectors.mapping(Employee::getPerson, Collectors.toSet())));

        Map<String, Set<Person>> expected = new HashMap<>();
        expected.put("QA", new HashSet<>(
                Arrays.asList(employees.get(2).getPerson(), employees.get(5).getPerson())));
        expected.put("dev", Collections.singleton(employees.get(0).getPerson()));
        expected.put("tester", new HashSet<>(Arrays.asList(
                employees.get(1).getPerson(),
                employees.get(3).getPerson(),
                employees.get(4).getPerson()))
        );
        assertEquals(expected, result);
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
