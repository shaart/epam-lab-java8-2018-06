package lambda.part2.exercise;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.function.Predicate;
import lambda.data.Person;
import org.junit.Test;

@SuppressWarnings({"ConstantConditions", "unused"})
public class Exercise2 {

    private Predicate<Person> getPersonHasEmptyFirstNamePredicate(){
        return person -> person.getFirstName() == null || person.getFirstName().isEmpty();
    }
    private Predicate<Person> getPersonHasEmptyLastNamePredicate(){
        return person -> person.getLastName() == null || person.getLastName().isEmpty();
    }

    @Test
    public void personHasNotEmptyLastNameAndFirstName() {
        // Предикат Person -> boolean, проверяющий что имя и фамилия человека не пусты
        Predicate<Person> validate = person -> person.getFirstName() != null
                && person.getLastName() != null
                && !person.getFirstName().isEmpty()
                && !person.getLastName().isEmpty();

        assertTrue(validate.test(new Person("Алексей", "Доренко", 40)));
        assertFalse(validate.test(new Person("Николай", "", 30)));
        assertFalse(validate.test(new Person("", "Мельников", 20)));
    }

    // Метод (Person -> boolean) -> (Person -> boolean)
    // Возвращает новый предикат, являющийся отрицанием исходного
    // При реализации использовать логический оператор !
    private Predicate<Person> negateUsingLogicalOperator(Predicate<Person> predicate) {
        return person -> !predicate.test(person);
    }

    // Метод (Person -> boolean, Person -> boolean) -> (Person -> boolean)
    // Возвращает новый предикат, объединяющий исходные с помощью операции "AND"
    // При реализации использовать логический оператор &&
    private Predicate<Person> andUsingLogicalOperator(Predicate<Person> left,
            Predicate<Person> right) {
        return person -> left.test(person) && right.test(person);
    }

    @Test
    public void personHasNotEmptyLastNameAndFirstNameUsingLogicalOperators() {
        Predicate<Person> personHasEmptyFirstName = getPersonHasEmptyFirstNamePredicate();
        Predicate<Person> personHasEmptyLastName = getPersonHasEmptyLastNamePredicate();

        Predicate<Person> personHasNotEmptyFirstName =
                negateUsingLogicalOperator(personHasEmptyFirstName);
        Predicate<Person> personHasNotEmptyLastName =
                negateUsingLogicalOperator(personHasEmptyLastName);

        Predicate<Person> personHasNotEmptyLastNameAndFirstName =
                andUsingLogicalOperator(personHasNotEmptyFirstName, personHasNotEmptyLastName);

        assertTrue(personHasNotEmptyLastNameAndFirstName.test(
                new Person("Алексей", "Доренко", 40)));
        assertFalse(personHasNotEmptyLastNameAndFirstName.test(
                new Person("Николай", "", 30)));
        assertFalse(personHasNotEmptyLastNameAndFirstName.test(
                new Person("", "Мельников", 20)));
    }

    // Метод (T -> boolean) -> (T -> boolean)
    // Возвращает новый предикат, являющийся отрицанием исходного
    // При реализации использовать логический оператор !
    private <T> Predicate<T> negate(Predicate<T> predicate) {
        return element -> !predicate.test(element);
    }

    // Метод (T -> boolean, T -> boolean) -> (T -> boolean)
    // Возвращает новый предикат, объединяющий исходные с помощью операции "AND"
    // При реализации использовать логический оператор &&
    private <T> Predicate<T> and(Predicate<T> left, Predicate<T> right) {
        return element -> left.test(element) && right.test(element);
    }

    @Test
    public void personHasNotEmptyLastNameAndFirstNameUsingGenericPredicates() {
        Predicate<Person> personHasEmptyFirstName = getPersonHasEmptyFirstNamePredicate();
        Predicate<Person> personHasEmptyLastName = getPersonHasEmptyLastNamePredicate();

        Predicate<Person> personHasNotEmptyFirstName = negate(personHasEmptyFirstName);
        Predicate<Person> personHasNotEmptyLastName = negate(personHasEmptyLastName);

        Predicate<Person> personHasNotEmptyLastNameAndFirstName =
                and(personHasNotEmptyFirstName, personHasNotEmptyLastName);

        assertTrue(personHasNotEmptyLastNameAndFirstName.test(
                new Person("Алексей", "Доренко", 40)));
        assertFalse(personHasNotEmptyLastNameAndFirstName.test(
                new Person("Николай", "", 30)));
        assertFalse(personHasNotEmptyLastNameAndFirstName.test(
                new Person("", "Мельников", 20)));
    }

    @Test
    public void personHasNotEmptyLastNameAndFirstNameUsingStandardMethods() {
        Predicate<Person> personHasEmptyFirstName = getPersonHasEmptyFirstNamePredicate();
        Predicate<Person> personHasEmptyLastName = getPersonHasEmptyLastNamePredicate();

        // Использовать Predicate.negate
        Predicate<Person> personHasNotEmptyFirstName = personHasEmptyFirstName.negate();
        Predicate<Person> personHasNotEmptyLastName = personHasEmptyLastName.negate();

        // Использовать Predicate.and
        Predicate<Person> personHasNotEmptyLastNameAndFirstName =
                personHasNotEmptyFirstName.and(personHasNotEmptyLastName);

        assertTrue(personHasNotEmptyLastNameAndFirstName.test(
                new Person("Алексей", "Доренко", 40)));
        assertFalse(personHasNotEmptyLastNameAndFirstName.test(
                new Person("Николай", "", 30)));
        assertFalse(personHasNotEmptyLastNameAndFirstName.test(
                new Person("", "Мельников", 20)));
    }
}
