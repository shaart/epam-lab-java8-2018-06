package streams.part2.exercise;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import org.junit.Test;

public class Exercise4 {

    /**
     * Выбирает из текста наиболее часто встречающиеся слова.
     * Подсчет слов выполняется без учета их регистра, т.е. "Привет", "привет", "пРиВеТ" - одно и то же слово.
     * Если некоторые слова имеют одинаковую частоту, то в выходном списке они упорядочиваются в лексикографическом порядке.
     * @param text Исходный текст в котором слова (в смешанном регистре) разделены пробелами.
     * @param numberWords Количество наиболее часто встречающихся слов, которые необходимо отобрать.
     * @return Список отобранных слов (в нижнем регистре).
     */
    private List<String> getFrequentlyOccurringWords(String text, int numberWords) {
        Function<Entry<String, Integer>, String> getWord = Entry::getKey;
        Comparator<Entry<String, Integer>> descendingByCount =
                (o1, o2) -> Integer.compare(o2.getValue(), o1.getValue());
        Comparator<Entry<String, Integer>> byAlphabet = Comparator.comparing(getWord);

        final String ONE_OR_MORE_SPACES_REGEX = "\\s+";
        final String NON_SMALL_LETTER_REGEX = "[^а-яёa-z]";
        final String EMPTY = "";
        
        return Arrays
                .stream(text.split(ONE_OR_MORE_SPACES_REGEX))
                .map(String::toLowerCase)
                .map(word -> word.replaceAll(NON_SMALL_LETTER_REGEX, EMPTY))
                .collect(toMap(
                        identity(),
                        word -> 1,
                        Integer::sum))
                .entrySet()
                .stream()
                .sorted(descendingByCount
                        .thenComparing(byAlphabet))
                .limit(numberWords)
                .map(getWord)
                .collect(toList());
    }

    @Test
    public void test1() {
        String source = "Мама мыла мыла мыла раму";

        List<String> result = getFrequentlyOccurringWords(source, 5);

        assertEquals(Arrays.asList("мыла", "мама", "раму"), result);
    }

    @Test
    public void test2() {
        String source = "Lorem ipsum dolor sit amet consectetur adipiscing elit Sed sodales consectetur purus at "
                      + "faucibus Donec mi quam tempor vel ipsum non faucibus suscipit massa Morbi lacinia velit "
                      + "blandit tincidunt efficitur Vestibulum eget metus imperdiet sapien laoreet faucibus Nunc "
                      + "eget vehicula mauris ac auctor lorem Lorem ipsum dolor sit amet consectetur adipiscing elit "
                      + "Integer vel odio nec mi tempor dignissim";

        List<String> result = getFrequentlyOccurringWords(source, 10);

        assertEquals(Arrays.asList("consectetur", "faucibus", "ipsum", "lorem", "adipiscing", "amet", "dolor", "eget", "elit", "mi"), result);
    }
}
