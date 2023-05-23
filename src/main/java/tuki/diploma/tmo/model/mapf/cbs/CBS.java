package tuki.diploma.tmo.model.mapf.cbs;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import tuki.diploma.tmo.model.core.Agent;
import tuki.diploma.tmo.model.core.Cell;
import tuki.diploma.tmo.model.core.LatticeMap;
import tuki.diploma.tmo.model.core.Path;
import tuki.diploma.tmo.model.mapf.cbs.ds.CBSNode;
import tuki.diploma.tmo.model.mapf.cbs.ds.Conflict;
import tuki.diploma.tmo.model.mapf.sspf.SSPF;

public class CBS implements MAPF {

    protected LatticeMap<Cell> map;
    protected CBSCostFunction costFunction;
    protected SSPF sspf;

    public CBS() {
    }

    public CBS(final CBSCostFunction costFunction, final SSPF llpf) {
        this.costFunction = costFunction;
        this.sspf = llpf;
    }

    public CBS(
            final CBSCostFunction costFunction,
            final SSPF llpf,
            final LatticeMap<Cell> map) {
        this.costFunction = costFunction;
        this.sspf = llpf;
        this.setMap(map);
    }

    public CBS(final LatticeMap<Cell> map) {
        this.map = map;
    }

    @Override
    public LatticeMap<Cell> getMap() {
        return this.map;
    }

    @Override
    public void setMap(final LatticeMap<Cell> map) {
        this.map = map;
        if (this.sspf != null)
            this.sspf.setMap(map);
    }

    public void setLLPF(final SSPF llpf) {
        this.sspf = llpf;
        if (this.map != null)
            this.sspf.setMap(map);
    }

    public void setCostFunction(final CBSCostFunction costFunction) {
        this.costFunction = costFunction;
    }

    @Override
    public Map<Agent, Path> findPaths() throws NullPointerException {
        return findPaths(map.getAgents(), map.getExits());
    }

    @Override
    public Map<Agent, Path> findPaths(
            final List<Agent> agents,
            final List<Cell> goals) {
        if (map == null)
            throw new NullPointerException("Lattice Map for CBS is null.");

        if (sspf == null)
            throw new NullPointerException("Low Level Path Finding algorithm for CBS in null.");

        return CBS.findPaths(map, agents, goals, sspf, costFunction);
    }

    // region STATIC

    public static Map<Agent, Path> findPaths(
            final LatticeMap<? extends Cell> map,
            final List<Agent> agents,
            final List<? extends Cell> goals,
            final SSPF llpf,
            final CBSCostFunction costFunction) {

        // init sspf and cost-function for root and all next children nodes
        final CBSNode root = new CBSNode(llpf, costFunction);
        root.updateSolution(agents);

        final PriorityQueue<CBSNode> OPEN = new PriorityQueue<>();
        OPEN.add(root);

        // path finding
        CBSNode newNode, currNode;
        Conflict conflict;
        while (!OPEN.isEmpty()) {
            currNode = OPEN.poll();
            if (currNode.isValid())
                return currNode.getSolution();

            conflict = currNode.getFirstConflict().get();

            // Add branch for 1'st agent
            newNode = currNode.child(conflict.getAgent1(), conflict.getStep());
            if (newNode.getCost() < Double.MAX_VALUE) // Solution was found
                OPEN.add(newNode);

            // Add branch for 2'nd agent
            newNode = currNode.child(conflict.getAgent2(), conflict.getStep());
            if (newNode.getCost() < Double.MAX_VALUE)
                OPEN.add(newNode);
        }

        return null;
    }

    // endregion
}
