package tuki.diploma.tmo.model.core;

import java.io.Serializable;

public record Coordinate(int x, int y) implements Serializable {

    public static Coordinate at(final int x, final int y) {
        return new Coordinate(x, y);
    }

    public boolean same(final int x, final int y) {
        return this.x == x && this.y == y;
    }

    public boolean same(final Coordinate coord) {
        return same(coord.x, coord.y);
    }
}
