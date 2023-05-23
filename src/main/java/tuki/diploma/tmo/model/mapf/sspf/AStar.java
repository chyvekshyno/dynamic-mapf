package tuki.diploma.tmo.model.mapf.sspf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import tuki.diploma.tmo.model.core.Cell;
import tuki.diploma.tmo.model.core.LatticeMap;
import tuki.diploma.tmo.model.core.Path;
import tuki.diploma.tmo.model.core.Step;
import tuki.diploma.tmo.model.utils.SimplePair2;

public class AStar implements SingleSorcePathFinderReservational {

    private LatticeMap<Cell> map;
    private Map<Cell, Double> costs;
    private HeuristicFunction heuristic;

    public AStar() {
        super();
    }

    public AStar(final HeuristicFunction heuristic) {
        this.heuristic = heuristic;
        this.costs = new HashMap<>();
    }

    public AStar(final LatticeMap<Cell> map, final HeuristicFunction heuristic) {
        this.map = map;
        this.heuristic = heuristic;
        this.costs = new HashMap<>();
    }

    @Override
    public HeuristicFunction getHeuristicFunction() {
        return this.heuristic;
    }

    @Override
    public void setHeuristicFunction(final HeuristicFunction heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public void setMap(final LatticeMap<Cell> map) {
        this.map = map;
    }

    @Override
    public LatticeMap<Cell> getMap() {
        return this.map;
    }

    @Override
    public Map<Cell, Double> getCosts() {
        return this.costs;
    }

    @Override
    public Path findPath(final Cell source, final Cell goal) {
        if (this.map == null) {
            // TODO: implement throwing error
            return null;
        }

        var result = AStar.findPath(this.map, source, goal, this.heuristic);
        this.costs = result.second();

        return result.first();
    }

    @Override
    public Path findPathWithReservation(final Cell source, final Cell goal, final List<Step> reserved) {
        if (this.map == null) {
            // TODO: implement throwing error
            return null;
        }

        var result = AStar.findPathWithReservation(
                this.map,
                source,
                goal,
                this.heuristic,
                reserved);
        this.costs = result.second();

        return result.first();
    }

    // region STATIC

    protected static List<Cell> reconstructPath(final Map<Cell, Cell> cameFrom, Cell current) {
        final List<Cell> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    protected static Path reconstructPath(
            final Cell current,
            final Map<Cell, Cell> cameFrom,
            final Map<Cell, Integer> scoreG) {
        var path = new PriorityQueue<Step>();
        var currCell = current;
        while (currCell != null) {
            path.add(Step.of(currCell, scoreG.get(currCell)));
            currCell = cameFrom.get(currCell);
        }
        return new Path(path);
    }

    protected static boolean endCondition(final Cell cell, final Cell goal) {
        return cell.atSameCoord(goal);
    }

    private static boolean isReserved(
            final Integer time,
            final Cell cell,
            final List<Step> reserved) {
        return reserved.stream()
                .anyMatch(s -> s.time() == time && s.cell().atSameCoord(cell));
    }

    public static SimplePair2<Path, Map<Cell, Double>> findPath(
            final LatticeMap<Cell> map,
            final Cell source,
            final Cell goal,
            final HeuristicFunction heuristic) {
        return AStar.findPathWithReservation(map, source, goal, heuristic, null);
    }

    /**
     * @brief Finds path from `source` position to `goal` position
     *        Using heuristic function and defined distance between cells
     *        Take distance between cells as a time needed to travel it.
     *
     * @param map       regular lattice-graph where pathfinding executes
     * @param source    final position algorithm try to find path to it
     * @param goal      start position algorithm try to find path from it
     * @param heuristic function estimates distance from nodes to goal
     * @param reserved  collection of reserved nodes at defined time steps
     * @return path (sequence of pairs of timestep and position) from source to goal
     *         node
     */
    public static SimplePair2<Path, Map<Cell, Double>> findPathWithReservation(
            final LatticeMap<Cell> map,
            final Cell source,
            final Cell goal,
            final HeuristicFunction heuristic,
            final List<Step> reserved) {
        // initialize data
        final Map<Cell, Double> scoreF = new HashMap<>();
        final Map<Cell, Double> scoreH = new HashMap<>();
        final Map<Cell, Integer> scoreG = new HashMap<>();

        final PriorityQueue<Cell> OPEN = new PriorityQueue<>(
                (c1, c2) -> Double.compare(scoreF.get(c1), scoreF.get(c2)));

        final Set<Cell> CLOSED = new HashSet<>();
        final Map<Cell, Cell> cameFrom = new HashMap<>();

        scoreG.put(source, 0);
        scoreH.put(source, heuristic.distance(source, goal));
        scoreF.put(source, scoreH.get(source));

        OPEN.add(source);

        int tentativeG = 0;
        Cell current;
        Cell neighbor;

        // path finding
        while (!OPEN.isEmpty()) {
            current = OPEN.poll();

            if (endCondition(current, goal))
                return SimplePair2.of(
                        reconstructPath(current, cameFrom, scoreG),
                        scoreF);

            CLOSED.add(current);

            for (final var directedNeighbor : map.getNeighborsDirs(current).entrySet()) {
                neighbor = directedNeighbor.getValue();

                if (CLOSED.contains(neighbor))
                    continue;

                tentativeG = scoreG.get(current) + directedNeighbor.getKey().dist();

                if (reserved != null && isReserved(tentativeG, neighbor, reserved)) // TODO: hardcoded tentativeG
                    continue;

                if (!OPEN.contains(neighbor) || tentativeG < scoreG.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    scoreG.put(neighbor, tentativeG);
                    scoreF.put(neighbor, tentativeG + heuristic.distance(neighbor, goal));

                    if (!OPEN.contains(neighbor))
                        OPEN.add(neighbor);
                }
            }
        }
        return SimplePair2.of(Path.empty(), new HashMap<>());
    }
    // endregion
}
