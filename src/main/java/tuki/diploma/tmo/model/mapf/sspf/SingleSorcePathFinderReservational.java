package tuki.diploma.tmo.model.mapf.sspf;

import java.util.List;

import tuki.diploma.tmo.model.core.Cell;
import tuki.diploma.tmo.model.core.Path;
import tuki.diploma.tmo.model.core.Step;

public interface SingleSorcePathFinderReservational extends SingleSourcePathFinder {

    Path findPathWithReservation(Cell source, Cell goal, List<Step> reserved);
}
