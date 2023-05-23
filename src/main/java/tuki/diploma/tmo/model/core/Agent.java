package tuki.diploma.tmo.model.core;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tuki.diploma.tmo.model.mapf.cbs.ds.Conflict;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Agent implements Serializable {

    private Cell position;
    private transient Path path;

    public Agent(final Cell position) {
        this.position = position;
    }

    public List<Conflict> findConflicts(final Agent other) {
        if (this.path == null)
            throw new NullPointerException("Can not find conflicts | agent's path is null.");

        if (other.getPath() == null)
            throw new NullPointerException("Can not find conflicts | agent's path is null.");

        return path.findConflictSteps(other.getPath()).stream()
                .map(step -> new Conflict(this, other, step))
                .toList();
    }

    public Agent(final int x, final int y) {
        this(Cell.at(x, y));
    }

    public void pathStep() {
        if (path == null || path.isEmpty())
            return;

        this.position = path.step();
    }
}
