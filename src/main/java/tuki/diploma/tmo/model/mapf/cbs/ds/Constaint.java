package tuki.diploma.tmo.model.mapf.cbs.ds;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import tuki.diploma.tmo.model.core.Agent;
import tuki.diploma.tmo.model.core.Cell;

@Data
@RequiredArgsConstructor
public class Constaint {
    final private Agent agent;
    final private Cell cell;
    final private Integer timestep;

    public static Constaint of(final Agent agent, final Cell cell, final Integer timestep) {
        return new Constaint(agent, cell, timestep);
    }
}
