package spliterators.example5;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TakeWhileSpliterator<T> extends Spliterators.AbstractSpliterator<T> {

    private final Spliterator<T> source;
    private final Predicate<? super T> predicate;
    private boolean isStoppedTaking;

    public TakeWhileSpliterator(Spliterator<T> source, Predicate<? super T> predicate) {
        super(source.estimateSize(), source.characteristics() & ~(SIZED | SUBSIZED));
        this.source = source;
        this.predicate = predicate;
        isStoppedTaking = false;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (isStoppedTaking) {
            return false;
        }
        source.tryAdvance(value -> {
            if (predicate.test(value)) {
                action.accept(value);
            } else {
                isStoppedTaking = true;
            }
        });
        return !isStoppedTaking;
    }

    // Other methods of spliterator

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getExactSizeIfKnown() {
        return -1;
    }

    @Override
    public Spliterator<T> trySplit() {
        throw new UnsupportedOperationException("Multithreading not supported for this operation");
    }
}
