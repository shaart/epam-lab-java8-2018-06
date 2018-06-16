package streams.part2.exercise;

import static org.junit.Assert.assertEquals;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;

public class Exercise3 {

    @Test
    public void createLimitedStringWithOddNumbersSeparatedBySpaces() {
        int countNumbers = 10;

        final int INIT_VALUE = 1;
        final int STEP = 2;
        String result = IntStream
                .iterate(INIT_VALUE, value -> value + STEP)
                .limit(countNumbers)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(" "));

        assertEquals("1 3 5 7 9 11 13 15 17 19", result);
    }

    @Test
    public void extractEvenNumberedCharactersToNewString() {
        String source = "abcdefghijklm";

        char[] chars = source.toCharArray();
        final int EVEN = 2;

        String result = IntStream.range(0, chars.length)
                                 .filter(index -> index % EVEN == 0)
                                 .collect(StringBuilder::new,
                                         (builder, index) -> builder.append(chars[index]),
                                         StringBuilder::append)
                                 .toString();

        assertEquals("acegikm", result);
    }
}
