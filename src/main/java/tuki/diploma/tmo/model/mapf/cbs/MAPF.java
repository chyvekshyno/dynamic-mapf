package tuki.diploma.tmo.model.mapf.cbs;

import java.util.List;
import java.util.Map;

import tuki.diploma.tmo.model.core.Agent;
import tuki.diploma.tmo.model.core.Cell;
import tuki.diploma.tmo.model.core.LatticeMap;
import tuki.diploma.tmo.model.core.Path;

public interface MAPF {

    /**
     * Finds paths for each agent inside the map
     * throws a NullPointerException if map is undefined
     * 
     * @return map of agents and their founded paths
     */
    Map<Agent, Path> findPaths() throws NullPointerException;

    /**
     * Finds paths for each agent inside the map
     * throws a NullPointerException if map is undefined
     * 
     * @param sources list of agent's positions inside the map
     * @param goals   list of exit's positions inside the map
     * @return map of agents and their founded paths
     */
    Map<Agent, Path> findPaths(final List<Agent> agents, final List<Cell> goals) throws NullPointerException;

    void setMap(final LatticeMap<Cell> map);

    LatticeMap<Cell> getMap();
}
