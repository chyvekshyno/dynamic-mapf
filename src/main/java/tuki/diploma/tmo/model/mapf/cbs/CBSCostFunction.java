package tuki.diploma.tmo.model.mapf.cbs;

import java.util.Collection;
import java.util.function.Function;

import tuki.diploma.tmo.model.core.Path;

public enum CBSCostFunction {
    SIC(paths -> paths.stream().mapToDouble(Path::getCost).sum());

    private final Function<Collection<Path>, Double> costFunction;

    public Double cost(final Collection<Path> paths) {
        return costFunction.apply(paths);
    }

    private CBSCostFunction(final Function<Collection<Path>, Double> costFunction) {
        this.costFunction = costFunction;
    }
}
