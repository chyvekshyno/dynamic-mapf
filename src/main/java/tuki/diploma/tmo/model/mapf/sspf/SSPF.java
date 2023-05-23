package tuki.diploma.tmo.model.mapf.sspf;

import java.util.List;
import java.util.Map;

import tuki.diploma.tmo.model.core.Cell;
import tuki.diploma.tmo.model.core.LatticeMap;
import tuki.diploma.tmo.model.core.Path;
import tuki.diploma.tmo.model.core.Step;

public enum SSPF {
    AStar(new AStar(HeuristicFunction.EUCLIDIAN)),
    StateTimeAStar(new StateTimeAStar()),
    SSIP(new SSIP());

    private final SingleSorcePathFinderReservational sspf;

    public Path findPath(Cell source, Cell goal, List<Step> reserved) {
        return sspf.findPathWithReservation(source, goal, reserved);
    }

    public void setMap(final LatticeMap<Cell> map) {
        this.sspf.setMap(map);
    }

    public LatticeMap<Cell> getMap() {
        return sspf.getMap();
    }

    public void setHeuristicFunction(final HeuristicFunction heuristic) {
        this.sspf.setHeuristicFunction(heuristic);
    }

    public HeuristicFunction getHeuristicFunction() {
        return sspf.getHeuristicFunction();
    }

    public Map<Cell, Double> getCostTable() {
        return sspf.getCosts();
    }

    SSPF(final SingleSorcePathFinderReservational sspf) {
        this.sspf = sspf;
    }

    SSPF(final SingleSorcePathFinderReservational sspf, final HeuristicFunction heuristic) {
        this.sspf = sspf;
    }

}
