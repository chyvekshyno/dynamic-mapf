package tuki.diploma.tmo.model.utils;

public record SimplePair<T>(T first, T second) {

    public static <T> SimplePair<T> of(final T first, final T second) {
        return new SimplePair<T>(first, second);
    }
}
