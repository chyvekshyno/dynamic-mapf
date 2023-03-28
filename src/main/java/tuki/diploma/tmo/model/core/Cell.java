package tuki.diploma.tmo.model.core;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@AllArgsConstructor
@Data
public class Cell {

    @NonNull
    private final Coordinate coord;
    private double dangerLevel;
    private double trafficLevel;
    private double heuristic;
    private boolean isWalkable;

    public Cell(final Coordinate coord) {
        this.coord = coord;
    }

    public Cell(final int x, final int y) {
        this(new Coordinate(x, y));
    }

    @Override
    public int hashCode() {
        return Objects.hash(coord, trafficLevel, heuristic);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Cell other = (Cell) obj;
        return Objects.equals(coord, other.coord);
    }

    public static Cell at(final int x, final int y) {
        return new Cell(x, y);
    }

    public boolean atSameCoord(final int x, final int y) {
        return this.coord.x() == x
            && this.coord.y() == y;
    }

    public boolean atSameCoord(final Coordinate coord) {
        return this.atSameCoord(coord.x(), coord.y());
    }

    public boolean atSameCoord(final Cell otherCell) {
        return this.atSameCoord(otherCell.getCoord());
    }
}
