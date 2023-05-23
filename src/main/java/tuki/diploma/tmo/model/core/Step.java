package tuki.diploma.tmo.model.core;

public record Step(Cell cell, Integer time) implements Comparable<Step> {

    public static Step of(final Cell cell, final Integer time) {
        return new Step(cell, time);
    }

    @Override
    public int compareTo(final Step other) {
        return Integer.compare(this.time(), other.time());
    }
}
