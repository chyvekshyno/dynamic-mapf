package tuki.diploma.tmo.model.mapf.sspf;

import java.util.List;

import tuki.diploma.tmo.model.core.Cell;
import tuki.diploma.tmo.model.core.Path;

public interface DynamicSSPF extends SingleSourcePathFinder {
    
    Path refindWithChanges(final Cell source, final List<Cell> changed);
}
