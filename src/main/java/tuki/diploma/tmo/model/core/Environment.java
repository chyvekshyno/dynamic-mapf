package tuki.diploma.tmo.model.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import tuki.diploma.tmo.model.ca.CellularAutomata;
import tuki.diploma.tmo.model.ca.FireCell;
import tuki.diploma.tmo.model.ca.FireCell.FireState;

@Data
@EqualsAndHashCode(callSuper = false)
public class Environment
        extends LatticeMap<FireCell>
        implements CellularAutomata<FireCell> {


    public Environment(int width, int height) {
        this(width, height, FireCell.class);
    }

    private Environment(int width, int height, Class<FireCell> clazz) {
        super(width, height, clazz);
    }


    @Override
    public void iterate() {
        // create random order for map
        var randomOrderCoords = getRandomOrder();

        // iterate through each cell to get new state
        FireCell cell;
        for (var coord : randomOrderCoords) {
            cell = this.getCell(coord);
            next(cell, this.getNeighbors(cell));
        }
    }

    @Override
    public void next(FireCell cell, List<FireCell> neighbors) {
        switch (cell.getState()) {
            case UNBURNED -> {
                double burnProb = calculateBurnProb(cell, neighbors);
                if (burnProb > Math.random())
                    cell.setState(FireState.BURNING);
            }
            case BURNING -> {
                cell.burning();
                if (cell.getBurningTime() > FireCell.IGNITION_TIME)
                    cell.setState(FireState.BURNED);
            }
            case BURNED, EMPTY -> {
            }
        }
    }

    private static double calculateBurnProb(final FireCell cell, List<FireCell> neighbors) {
        // TODO: implement method `calculateBurnProb`
        return 0;
    }

    private List<Coordinate> getRandomOrder() {
        List<Coordinate> allCoords = new ArrayList<>();
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                allCoords.add(Coordinate.at(i, j));
            }
        }
        Collections.shuffle(allCoords);
        return allCoords;
    }

}
