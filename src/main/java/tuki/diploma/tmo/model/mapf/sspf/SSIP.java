package tuki.diploma.tmo.model.mapf.sspf;

import java.util.List;
import java.util.Map;

import tuki.diploma.tmo.model.core.Cell;
import tuki.diploma.tmo.model.core.LatticeMap;
import tuki.diploma.tmo.model.core.Path;
import tuki.diploma.tmo.model.core.Step;

public class SSIP implements SingleSorcePathFinderReservational {

    @Override
    public Path findPathWithReservation(Cell source, Cell goal, List<Step> reserved) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findPathWithReservation'");
    }

    @Override
    public void setMap(LatticeMap map) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setMap'");
    }

    @Override
    public LatticeMap getMap() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMap'");
    }

    @Override
    public Path findPath(Cell source, Cell goal) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findPath'");
    }

    @Override
    public Map<Cell, Double> getCosts() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCosts'");
    }

    @Override
    public void setHeuristicFunction(HeuristicFunction heuristic) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setHeuristicFunction'");
    }

    @Override
    public HeuristicFunction getHeuristicFunction() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHeuristicFunction'");
    }
}
