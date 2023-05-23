package tuki.diploma.tmo.controllers;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import tuki.diploma.tmo.model.core.Agent;
import tuki.diploma.tmo.model.core.Cell;
import tuki.diploma.tmo.model.core.LatticeMap;
import tuki.diploma.tmo.model.mapf.cbs.MAPF;

@NoArgsConstructor
@AllArgsConstructor
public class PathFindingController {

    private LatticeMap<Cell> map;
    private MAPF mapf;

    public PathFindingController(final LatticeMap<Cell> map) {
        this.map = map;
    }

    public void setMAPF(MAPF mapf) {
        this.mapf = mapf;

        if (this.map != null)
            this.mapf.setMap(this.map);
    }

    public LatticeMap<? extends Cell> getMap() {
        return map;
    }

    public <T extends Cell> void setMap(final LatticeMap<T> map) {
        if (mapf != null)
            mapf.setMap(this.map);
    }

    /**
     * Finds paths for each agents inside the map in such way they does not
     * interfere each other.
     */
    public void findPathsMA() {
        findPathsMA(map.getAgents(), map.getExits());
    }

    /**
     * Finds paths for each agents inside the map in such way they does not
     * interfere each other.
     * 
     * @param agents list of agents finding paths MA for them.
     * @param goals  list of goal cells mapf finds paths to them.
     */
    public void findPathsMA(final List<Agent> agents, final List<Cell> goals) {
        var paths = mapf.findPaths(agents, goals);
        for (var agent : agents) {
            agent.setPath(paths.get(agent));
        }
    }

}
