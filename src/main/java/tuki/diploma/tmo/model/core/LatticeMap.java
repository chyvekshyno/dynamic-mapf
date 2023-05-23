package tuki.diploma.tmo.model.core;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.Data;

@Data
public class LatticeMap<T extends Cell> {

    public static Integer DIST_TO_NEIGHBOR = 1;
    public static Integer DIST_TO_NEIGH_DIAGONAL = 1;

    public enum Direction {
        NORTH(1, 0, DIST_TO_NEIGHBOR),
        EAST(0, 1, DIST_TO_NEIGHBOR),
        SOUTH(-1, 0, DIST_TO_NEIGHBOR),
        WEST(0, -1, DIST_TO_NEIGHBOR),
        NE(1, 1, DIST_TO_NEIGH_DIAGONAL),
        SE(-1, 1, DIST_TO_NEIGH_DIAGONAL),
        SW(-1, -1, DIST_TO_NEIGH_DIAGONAL),
        NW(1, -1, DIST_TO_NEIGH_DIAGONAL);

        private final Integer dx;
        private final Integer dy;
        private final Integer distance;

        private Direction(
                final Integer dx,
                final Integer dy,
                final Integer distance) {
            this.dx = dx;
            this.dy = dy;
            this.distance = distance;
        }

        public Integer dX() {
            return dx;
        }

        public Integer dY() {
            return dy;
        }

        public Integer dist() {
            return distance;
        }
    }

    private final Class<T> cellClass;

    protected final int width;
    protected final int height;
    protected final List<List<T>> grid;
    private final List<Cell> exits;
    private final List<Agent> agents;

    public LatticeMap(final int width, final int height, Class<T> clazz) {
        this.cellClass = clazz;
        this.width = width;
        this.height = height;
        this.grid = new ArrayList<>();
        this.exits = new ArrayList<>();
        this.agents = new ArrayList<>();

        initMap();
    }

    private void initMap() {
        try {
            final Constructor<T> cellCtor = cellClass.getConstructor(Integer.class, Integer.class);
            List<T> cellRow;
            for (int x = 0; x < width; x++) {
                cellRow = new ArrayList<>();
                for (int y = 0; y < height; y++) {
                    cellRow.add(cellCtor.newInstance(x, y));
                }
                this.grid.add(cellRow);
            }
        } catch (Exception e) {
            System.err.println("ERROR while creating lattice map: " + e.getMessage());
        }

    }

    // region Methods

    public T getCell(final int x, final int y) {
        return this.grid.get(x).get(y);
    }

    public T getCell(final Coordinate coord) {
        return getCell(coord.x(), coord.y());
    }

    public void addExit(final Cell cell) {
        exits.add(cell);
    }

    public void removeExit(final Cell cell) {
        exits.remove(cell);
    }

    // region EXITS & AGENTS HANDLING

    public void addAgent(final Agent agent) {
        this.agents.add(agent);
    }

    public void removeAgent(final Agent agent) {
        this.agents.remove(agent);
    }

    public void removeAgent(final Cell cell) {
        agents.removeIf(a -> a.getPosition() == cell);
    }

    public void removeAgent(final Coordinate coord) {
        agents.removeIf(a -> a.getPosition().getCoord() == coord);
    }

    public void removeAgent(final int x, final int y) {
        agents.removeIf(a -> a.getPosition().getCoord().x() == x
                && a.getPosition().getCoord().y() == y);
    }

    public void becomeLeader(final Follower follower) {
        agents.remove(follower);
        final var newLeader = new Leader(follower.getPosition());
        agents.add(newLeader);
    }
    // endregion

    private boolean isValidPosition(final int x, final int y) {
        return x >= 0 && x < width
                && y >= 0 && y < height;
    }

    public List<T> getNeighbors(final T cell) {
        final List<T> retval = new ArrayList<>();

        if (!cell.isWalkable())
            return retval;

        Optional<T> optNeighbor;
        T neighbor;
        for (var dir : Direction.values()) {
            optNeighbor = getNeighbor(cell, dir);
            if (getNeighbor(cell, dir).isEmpty())
                continue;

            neighbor = optNeighbor.get();
            if (!neighbor.isWalkable())
                continue;

            if (isTrueNeighbor(cell, dir))
                retval.add(neighbor);
        }

        return getNeighborsDirs(cell).values().stream().toList();
    }

    public Map<Direction, T> getNeighborsDirs(final T cell) {
        final Map<Direction, T> retval = new HashMap<>();

        if (!cell.isWalkable())
            return retval;

        Optional<T> optNeighbor;
        T neighbor;
        for (var dir : Direction.values()) {
            optNeighbor = getNeighbor(cell, dir);
            if (getNeighbor(cell, dir).isEmpty() || !optNeighbor.get().isWalkable())
                continue;

            neighbor = optNeighbor.get();
            if (isTrueNeighbor(cell, dir))
                retval.put(dir, neighbor);
        }

        return retval;
    }

    private boolean isTrueNeighbor(T cell, Direction dir) {
        return switch (dir) {
            case EAST, NORTH, SOUTH, WEST -> true;
            case NE -> getNeighbor(cell, Direction.NORTH).get().isWalkable()
                    && getNeighbor(cell, Direction.EAST).get().isWalkable();

            case NW -> getNeighbor(cell, Direction.NORTH).get().isWalkable()
                    && getNeighbor(cell, Direction.WEST).get().isWalkable();

            case SE -> getNeighbor(cell, Direction.SOUTH).get().isWalkable()
                    && getNeighbor(cell, Direction.EAST).get().isWalkable();

            case SW -> getNeighbor(cell, Direction.SOUTH).get().isWalkable()
                    && getNeighbor(cell, Direction.WEST).get().isWalkable();
        };
    }

    public Optional<T> getNeighbor(final T cell, final Direction dir) {
        final int neighborX = cell.X() + dir.dX();
        final int neighborY = cell.Y() + dir.dY();

        if (!isValidPosition(neighborX, neighborY)) {
            return Optional.empty();
        }
        return Optional.of(getCell(neighborX, neighborY));
    }
    // endregion
}
