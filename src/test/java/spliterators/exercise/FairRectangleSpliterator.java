package spliterators.exercise;

import java.util.Arrays;
import java.util.Objects;
import java.util.Spliterators;
import java.util.function.IntConsumer;

/**
 * Сплитератор, оборачивающий прямоугольную матрицу int[][]
 * Обходит элементы слева-направо, сверху-вниз
 * Деление "честное" - по количеству элементов
 */
public class FairRectangleSpliterator extends Spliterators.AbstractIntSpliterator {

    private static final int ELEMENTS_PER_SPLITERATOR = 7;

    private final int[][] data;
    private final int rowLength;

    private final int indexEndExclusively;
    private final int rowEndExclusively;

    private int index;
    private int row;

    /**
     *  0  1  2  3  4<br>
     *  5  6  / 7  8  9<br>
     * 10 11 12 13 14
     */
    public FairRectangleSpliterator(int[][] data) {
        this(data, 0, tryGetRowNumber(data), 0, tryGetRowLength(data));
    }

    private FairRectangleSpliterator(int[][] data, int row, int rowEndExclusively, int index,
            int indexEndExclusively) {
        super(tryCalcSize(data), SIZED | SUBSIZED | NONNULL | ORDERED | IMMUTABLE);

        this.data = data;
        this.row = row;
        this.rowEndExclusively = rowEndExclusively;
        this.index = index;
        this.indexEndExclusively = indexEndExclusively;
        this.rowLength = data[0].length;
    }

    private int calcSize(int rowLength,
            int index, int indexEndExclusively,
            int row, int rowEndExclusively) {
        return (rowEndExclusively - row - 1) * rowLength + indexEndExclusively - index;
    }

    @Override
    public long estimateSize() {
        return calcSize(rowLength, index, indexEndExclusively, row, rowEndExclusively);
    }

    @Override
    public void forEachRemaining(IntConsumer action) {
        while (row < rowEndExclusively) {
            action.accept(data[row][index++]);
            if (index >= rowLength) {
                index = 0;
                row++;
            }
        }
    }

    @Override
    public long getExactSizeIfKnown() {
        return estimateSize();
    }

    @Override
    public boolean tryAdvance(IntConsumer action) {
        if (row > rowEndExclusively) {
            return false;
        }
        action.accept(data[row][index++]);
        if (index >= rowLength) {
            index = 0;
            row++;
        }
        return true;
    }

    @Override
    public OfInt trySplit() {
        if (estimateSize() <= ELEMENTS_PER_SPLITERATOR) {
            return null;
        }

        int newRow = row;
        int newIndex = index;
        int newRowEndExcl;
        int newIndexEndExcl;
        if (indexEndExclusively - index >= ELEMENTS_PER_SPLITERATOR) {
            newIndexEndExcl = index = index + ELEMENTS_PER_SPLITERATOR;
            newRowEndExcl = row = row + 1;
        } else {
            /*
             * For example, split per 10 elements, when row = 3 and we are at 0,0 position:
             * 3 - 0 = 3
             * 10 - 3 = 7
             * 7 / 3 = 2
             * 7 - 2 * 3 = 1
             *
             * |1 2 3
             *  4 5 6
             *  7 8 9
             *  1|2 3
             *  4 5 6
             */
            int fromThisRow = rowLength - index;
            row++;
            int remainingToAdd = ELEMENTS_PER_SPLITERATOR - fromThisRow;
            int rowsNeed = remainingToAdd / rowLength;
            newRowEndExcl = row = row + rowsNeed + 1;
            remainingToAdd = remainingToAdd - rowsNeed * rowLength;
            newIndexEndExcl = index = remainingToAdd;
        }
        if (index >= rowLength) {
            newRowEndExcl = row = row + 1;
            newIndexEndExcl = index = 0;
        }

        return new FairRectangleSpliterator(data, newRow, newRowEndExcl, newIndex, newIndexEndExcl);
    }

    /**
     * Tries to calculate size of array.
     *
     * @param data Target 2D array
     * @return Size of 2D array
     * @throws NullPointerException if <code>data</code> is <code>null</code>
     * @throws IndexOutOfBoundsException if rows or columns are zero-length
     * @throws IllegalArgumentException if array is not rectangle
     */
    private static int tryCalcSize(int[][] data) {
        Objects.requireNonNull(data);
        final int rowLength = data[0].length;
        if (!Arrays.stream(data)
                   .allMatch(row -> row.length == rowLength)) {
            throw new IllegalArgumentException();
        }
        int rowsNum = data.length;

        return rowLength * rowsNum;
    }

    private static int tryGetRowLength(int[][] data) {
        Objects.requireNonNull(data);
        if (0 == data.length) {
            throw new IllegalArgumentException();
        }

        return data[0].length;
    }

    private static int tryGetRowNumber(int[][] data) {
        Objects.requireNonNull(data);

        return data.length;
    }
}