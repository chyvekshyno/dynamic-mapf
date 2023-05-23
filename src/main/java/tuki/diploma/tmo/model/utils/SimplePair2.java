package tuki.diploma.tmo.model.utils;

public record SimplePair2<T, Y>(T first, Y second) {

    public static <T, Y> SimplePair2<T, Y> of(final T first, final Y second) {
        return new SimplePair2<T, Y>(first, second);
    }
}

