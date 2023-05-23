package tuki.diploma.tmo.model.mapf.cbs.ds;

import java.util.Objects;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import tuki.diploma.tmo.model.core.Agent;
import tuki.diploma.tmo.model.core.Step;

@Data
@RequiredArgsConstructor
public class Conflict implements Comparable<Conflict> {

    public enum ConflictType {
        CARDINAL, SEMI_CARDINAL, NON_CARDINAL;
    }

    private final Agent agent1;
    private final Agent agent2;
    private final Step step;
    private ConflictType type;

    @Override
    public int compareTo(Conflict other) {
        return this.step.compareTo(other.getStep());
    }

    @Override
    public int hashCode() {
        return Objects.hash(agent1, agent2, step);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Conflict other = (Conflict) obj;
        return Objects.equals(step, other.step)
                && ((Objects.equals(agent1, other.agent1) && Objects.equals(agent2, other.agent2))
                        || (Objects.equals(agent1, other.agent2) && Objects.equals(agent2, other.agent1)));
    }
}
