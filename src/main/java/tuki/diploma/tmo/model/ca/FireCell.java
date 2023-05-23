package tuki.diploma.tmo.model.ca;

import tuki.diploma.tmo.model.core.Cell;
import tuki.diploma.tmo.model.core.Coordinate;

public class FireCell extends Cell implements AutomataCell {

    public final static int IGNITION_TIME = 10;

    public enum FireState {
        EMPTY, UNBURNED, BURNING, BURNED
    }

    private FireState state;
    private int burningTime;

    public FireCell(final int x, final int y) {
        super(x, y);
        this.state = FireState.UNBURNED;
        this.burningTime = 0;
    }

    public FireCell(final Coordinate coord) {
        this(coord.x(), coord.y());
    }

    public int getBurningTime() {
        return burningTime;
    }

    public FireState getState() {
        return state;
    }

    public void setState(FireState state) {
        this.state = state;
    }

    public void burning() {
        this.burningTime++;
    }

}
