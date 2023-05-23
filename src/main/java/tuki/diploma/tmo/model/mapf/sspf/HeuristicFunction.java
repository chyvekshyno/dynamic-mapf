package tuki.diploma.tmo.model.mapf.sspf;

import java.util.function.BiFunction;

import tuki.diploma.tmo.model.core.Cell;

public enum HeuristicFunction {
    EUCLIDIAN(HeuristicFunction::euclidian);

    private final BiFunction<Cell, Cell, Double> function;

    public double distance(final Cell cell1, final Cell cell2) {
        return function.apply(cell1, cell2);
    }

    private HeuristicFunction(final BiFunction<Cell, Cell, Double> function) {
        this.function = function;
    }

    private static double euclidian(final Cell c1, final Cell c2) {
        return Math.abs(c1.X() - c2.X()) - Math.abs(c1.Y() - c2.Y());
    }
}
