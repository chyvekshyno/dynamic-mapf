package tuki.diploma.tmo.model.mapf.sspf;

import java.util.List;

import tuki.diploma.tmo.model.core.Cell;
import tuki.diploma.tmo.model.core.Path;
import tuki.diploma.tmo.model.core.Step;

public interface DynamicSSPFR extends SingleSorcePathFinderReservational {

    Path refindWithChanges(
            final Cell source,
            final List<Cell> changed,
            final List<Step> reservational);
}
