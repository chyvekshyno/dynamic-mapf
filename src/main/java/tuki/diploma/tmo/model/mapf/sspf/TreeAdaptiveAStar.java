package tuki.diploma.tmo.model.mapf.sspf;

import java.security.InvalidParameterException;
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

public class TreeAdaptiveAStar implements DynamicSSPFR {

    private LatticeMap<Cell> map;
    private Map<Cell, Double> costs;
    private HeuristicFunction heuristic;

    private int counter;
    private Map<Cell, Integer> GENERATED;
    private Map<Cell, Integer> ID;
    private Map<Integer, Double> H_MIN;
    private Map<Integer, Double> H_MAX;
    private Map<Cell, Double> scoreH;

    private Cell goalCell;

    private Map<Cell, Cell> REUSABLE_TREE;
    private Map<Integer, List<Integer>> PATHS;

    public TreeAdaptiveAStar(HeuristicFunction heuristic) {
        this.heuristic = heuristic;
        initPF();
    }

    public TreeAdaptiveAStar(LatticeMap<Cell> map, HeuristicFunction heuristic) {
        this.map = map;
        this.heuristic = heuristic;
        initPF();
    }

    @Override
    public Map<Cell, Double> getCosts() {
        return this.costs;
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

    private void initPF() {
        this.counter = 0;
        this.H_MAX.put(0, -1.);
        map.getGrid().stream()
                .flatMap(List::stream)
                .forEach(c -> {
                    this.GENERATED.put(c, 0);
                    this.ID.put(c, 0);
                    this.REUSABLE_TREE.put(c, null);
                });
    }

    public void initCell(
            final Cell cell,
            Map<Cell, Double> scoreH,
            Map<Cell, Double> scoreG) {
        if (GENERATED.get(cell) == 0) {
            scoreG.put(cell, Double.MAX_VALUE);
            scoreH.put(cell, heuristic.distance(cell, goalCell));
        } else if (GENERATED.get(cell) != counter) {
            scoreG.put(cell, Double.MAX_VALUE);
        }
        GENERATED.put(cell, counter);
    }

    public void addPath(
            final Cell cell,
            final Cell startCell,
            final Map<Cell, Cell> path,
            Map<Cell, Double> scoreH) {
        if (cell != goalCell) {
            if (!PATHS.containsKey(ID.get(cell)))
                PATHS.put(ID.get(cell), new ArrayList<>());
            PATHS.get(ID.get(cell)).add(counter);
        }
        H_MIN.put(counter, scoreH.get(cell));
        H_MAX.put(counter, scoreH.get(startCell));

        Cell _cell0, _cell1;
        _cell1 = cell;
        while (cell != startCell) {
            _cell0 = _cell1;
            _cell1 = path.get(_cell1);
            ID.put(_cell1, counter);
            REUSABLE_TREE.put(_cell1, _cell0);
        }
    }

    public void removePath(
            final Cell cell,
            Map<Cell, Double> scoreH) {
        int pathId = ID.get(cell);
        if (H_MAX.get(pathId) > scoreH.get(REUSABLE_TREE.get(cell)))
            H_MAX.put(pathId, scoreH.get(REUSABLE_TREE.get(cell)));

        var pathQueue = new PriorityQueue<Integer>();
        for (var ind : PATHS.get(pathId)) {
            if (H_MAX.get(pathId) < H_MIN.get(ind)) {
                pathQueue.add(ind);
                PATHS.remove(ind);
            }
        }

        int ind = 0;
        while (!pathQueue.isEmpty()) {
            ind = pathQueue.poll();
            if (H_MAX.get(ind) > H_MIN.get(ind)) {
                H_MAX.put(ind, H_MIN.get(ind));
                for (int ind_next : PATHS.get(ind)) {
                    pathQueue.add(ind_next);
                    PATHS.remove(ind_next);
                }
            }
        }
    }

    public SimplePair2<Path, Map<Cell, Double>> computePath(
            final Cell source,
            final List<Step> reserved) {
        final Map<Cell, Double> scoreF = new HashMap<>();
        this.scoreH = new HashMap<>();
        final Map<Cell, Integer> scoreG = new HashMap<>();

        final PriorityQueue<Cell> OPEN = new PriorityQueue<>(
                (c1, c2) -> Double.compare(scoreF.get(c1), scoreF.get(c2)));

        final Set<Cell> CLOSED = new HashSet<>();
        final Map<Cell, Cell> cameFrom = new HashMap<>();

        scoreG.put(source, 0);
        scoreH.put(source, heuristic.distance(source, goalCell));
        scoreF.put(source, scoreH.get(source));

        OPEN.add(source);

        int tentativeG = 0;
        Cell current;
        Cell neighbor;

        // path finding
        while (!OPEN.isEmpty()) {
            current = OPEN.poll();

            if (endCondition(current, goalCell))
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
                    scoreF.put(neighbor, tentativeG + heuristic.distance(neighbor, goalCell));

                    if (!OPEN.contains(neighbor))
                        OPEN.add(neighbor);
                }
            }
        }
        return SimplePair2.of(Path.empty(), new HashMap<>());

    }

    @Override
    public Path findPath(Cell source, Cell goal) {
        return findPathWithReservation(source, goal, null);
    }

    @Override
    public Path findPathWithReservation(Cell source, Cell goal, List<Step> reserved) {
        if (this.map == null)
            throw new InvalidParameterException("Does NOT defined map for pathfinding");

        if (this.goalCell == null)
            this.goalCell = goal;
        else if (this.goalCell != goal)
            throw new InvalidParameterException("Redefined goal cell for dynamic pathfinding");

        var result = computePath(source, reserved);
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

    @Override
    public Path refindWithChanges(
            final Cell source,
            final List<Cell> changed,
            final List<Step> reservational) {
        for (Cell cell : changed) {
            if (REUSABLE_TREE.containsKey(cell))
                removePath(cell, scoreH);
        }

        counter++;
        return findPathWithReservation(source, this.goalCell, reservational);
    }

    // endregion

}
