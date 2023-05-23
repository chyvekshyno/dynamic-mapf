package tuki.diploma.tmo.model.mapf.sspf;

import java.util.Map;

import tuki.diploma.tmo.model.core.Cell;
import tuki.diploma.tmo.model.core.LatticeMap;
import tuki.diploma.tmo.model.core.Path;

public interface SingleSourcePathFinder {

    Path findPath(Cell source, Cell goal);

    Map<Cell, Double> getCosts();
    void setHeuristicFunction(HeuristicFunction heuristic);
    HeuristicFunction getHeuristicFunction();

    void setMap(LatticeMap<Cell> map);
    LatticeMap<Cell> getMap();
}
