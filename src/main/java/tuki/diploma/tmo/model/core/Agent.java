package tuki.diploma.tmo.model.core;

import java.util.List;

import lombok.Data;

@Data
public abstract class Agent {

    private final int id;
    private final Cell position;
    private List<Cell> path;

    public Agent(final int id, final Cell position) {
        this.id = id;
        this.position = position;
    }

    public Agent(final int id, final int x, final int y) {
        this(id, Cell.at(x, y));
    }

}
