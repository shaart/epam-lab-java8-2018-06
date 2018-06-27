package spliterators.example5;

import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import spliterators.example4.Pair;

/**
 * Make pair from your elements and elements of zipping stream. Streams with different size not
 * supported.
 *
 * @param <Z> Anything, zipping
 * @param <E> Anything, source
 */
public class ZipSpliterator<E, Z> extends Spliterators.AbstractSpliterator<Pair<E, Z>> {

    private static final int THRESHOLD = 100;

    private final Spliterator<Z> zipping;
    private final Spliterator<E> source;

    /**
     * Create new {@link ZipSpliterator} that makes pairs from your elements and elements of zipping
     * stream. If streams have different size exception will be thrown.
     *
     * @param source Source spliterator
     * @param zipping Zip spliterator
     */
    public ZipSpliterator(Spliterator<E> source, Spliterator<Z> zipping) {
        super(checkEstimateSize(source, zipping), mutualCharacteristics(source, zipping));

        this.source = source;
        this.zipping = zipping;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Pair<E, Z>> action) {
        return zipping.tryAdvance(zipValue ->
                source.tryAdvance(sourceValue ->
                        action.accept(new Pair<>(sourceValue, zipValue))));
    }

    // Other methods of spliterator

    @Override
    public int characteristics() {
        return source.characteristics() & zipping.characteristics();
    }

    @Override
    public long estimateSize() {
        return source.estimateSize();
    }

    @Override
    public long getExactSizeIfKnown() {
        return source.getExactSizeIfKnown();
    }

    @Override
    public boolean hasCharacteristics(int characteristics) {
        return source.hasCharacteristics(characteristics)
                && zipping.hasCharacteristics(characteristics);
    }

    @Override
    public Spliterator<Pair<E, Z>> trySplit() {
        if (source.estimateSize() < THRESHOLD) {
            return null;
        }
        Spliterator<E> prefixSource = source.trySplit();
        Spliterator<Z> prefixZip = zipping.trySplit();

        return prefixSource == null || prefixZip == null ? null
                : new ZipSpliterator<>(prefixSource, prefixZip);
    }

    private static long checkEstimateSize(Spliterator<?> source, Spliterator<?> another) {
        if (!Objects.equals(source.estimateSize(), another.estimateSize())) {
            throw new UnsupportedOperationException("Streams with different length not supported");
        }
        return source.estimateSize();
    }

    private static int mutualCharacteristics(Spliterator<?> source, Spliterator<?> zipping) {
        if (!source.hasCharacteristics(SUBSIZED) || !zipping.hasCharacteristics(SUBSIZED)) {
            throw new IllegalArgumentException("Spliterators must support SUBSIZED");
        }
        return source.characteristics() & zipping.characteristics();
    }
}
