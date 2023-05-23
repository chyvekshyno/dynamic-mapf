package tuki.diploma.tmo.model.ca;

import java.util.List;

public interface CellularAutomata<T extends AutomataCell> {

    /**
     * Main method that calculates new states for each cell of current CA.
     * Here User specifies how CA works calling operators to find new cells states.
     */
    void iterate();

    /**
     * Main method that finds state for this Cell for the next iteration
     * 
     * @param cell current cell
     * @param neighbors list of neighbors from AutomataGrid
     */
    public void next(T cell, List<T> neighbors);

}
