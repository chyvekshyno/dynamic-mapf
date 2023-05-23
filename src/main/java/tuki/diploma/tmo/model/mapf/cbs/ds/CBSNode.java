package tuki.diploma.tmo.model.mapf.cbs.ds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import tuki.diploma.tmo.model.core.Agent;
import tuki.diploma.tmo.model.core.Path;
import tuki.diploma.tmo.model.core.Step;
import tuki.diploma.tmo.model.mapf.cbs.CBSCostFunction;
import tuki.diploma.tmo.model.mapf.sspf.SSPF;

public class CBSNode implements Comparable<CBSNode> {

    private CBSCostFunction costFunction;
    private SSPF sspf;

    private Map<Agent, Path> solution;
    private Map<Agent, List<Step>> constraints;
    private Optional<Conflict> firstConflict;
    private Double cost;
    private int conflictNumber;

    public CBSNode(final SSPF sspf, final CBSCostFunction costFunction) {
        this(sspf, costFunction, new HashMap<>(), new HashMap<>());
    }

    public CBSNode(
            final SSPF sspf,
            final CBSCostFunction costFunction,
            final Map<Agent, List<Step>> constaints) {
        this(sspf, costFunction, constaints, null);
    }

    public CBSNode(
            SSPF sspf,
            CBSCostFunction costFunction,
            Map<Agent, List<Step>> constraints,
            Map<Agent, Path> solution,
            Agent toUpdate) {
        this(sspf, costFunction, constraints, solution, List.of(toUpdate));
    }

    public CBSNode(
            SSPF sspf,
            CBSCostFunction costFunction,
            Map<Agent, List<Step>> constraints,
            Map<Agent, Path> solution,
            List<Agent> toUpdate) {
        this(sspf, costFunction, constraints, solution);
        updateSolution(toUpdate);
    }

    public CBSNode(
            final SSPF sspf,
            final CBSCostFunction costFunction,
            final Map<Agent, List<Step>> constaints,
            final Map<Agent, Path> solution) {
        this.sspf = sspf;
        this.costFunction = costFunction;
        this.constraints = constaints;
        this.solution = solution;
    }

    @Override
    public int compareTo(final CBSNode other) {
        return Double.compare(this.cost, other.cost);
    }

    // region METHODS

    /**
     * Set another node solution to this node if both nodes have equal costs and
     * number of conflicts of another node is lower than currents node.
     *
     * @param node instance of CBSNode to which current node tries to adapt
     * @return if instance of CBSNode can be adopted to another instance
     */
    public boolean adopt(CBSNode node) {
        if (this.cost == node.getCost() && node.getConflictNumber() < this.conflictNumber) {
            this.solution = node.getSolution();
            return true;
        }

        return false;
    }

    public Map<Agent, Path> getSolution() {
        return solution;
    }

    public Map<Agent, List<Step>> getConstraints() {
        return constraints;
    }

    public int getConflictNumber() {
        return conflictNumber;
    }

    public List<Conflict> getConflicts() {
        // TODO: implement finding all conflicts using MDDs;
        return null;
    }

    public Optional<Conflict> getFirstConflict() {
        if (solution == null)
            throw new NullPointerException("To find any conflicts is needed to find solution firstly.");

        if (firstConflict == null)
            findFirstConflict();

        return firstConflict;
    }

    public Double getCost() {
        return cost;
    }

    public boolean isValid() {
        if (firstConflict == null)
            firstConflict = findFirstConflict();

        if (firstConflict.isPresent())
            return false;

        return true;
    }

    public void setCostFunction(final CBSCostFunction costFunction) {
        this.costFunction = costFunction;
    }

    public void setLowLevelPF(final SSPF sspf) {
        this.sspf = sspf;
    }

    /**
     * Generates CBSNode child with additional constraints.
     * The child node have the same low-level pathfinding algo and cost function.
     * Solution is updated considering agent occurs generating new child
     * 
     * @param agent         agent object in conflict to create a new constain
     * @param newConstraint step to add to constraint list
     * @return new CBSNode instance with updated constraint list
     */
    public CBSNode child(final Agent agent, final Step newConstraint) {
        final var newConstraints = new HashMap<>(this.constraints);

        if (!newConstraints.containsKey(agent))
            newConstraints.put(agent, new ArrayList<>());
        newConstraints.get(agent).add(newConstraint);

        return new CBSNode(this.sspf, this.costFunction, newConstraints, this.solution, agent);
    }

    /**
     * Generates CBSNode child with additional constraints.
     * The child node have the same low-level pathfinding algo and cost function.
     * Solution is updated considering agent occurs generating new child
     * 
     * @param agent         agent object in conflict to create a new constain
     * @param newConstraint list of steps to add them to constraint list
     * @return new CBSNode instance with updated constraint list
     */
    public CBSNode child(final Agent agent, final List<Step> newConstraint) {
        final var newConstraints = new HashMap<>(this.constraints);

        if (!newConstraints.containsKey(agent))
            newConstraints.put(agent, new ArrayList<>());
        newConstraints.get(agent).addAll(newConstraint);

        return new CBSNode(this.sspf, this.costFunction, newConstraints, this.solution, agent);
    }

    /**
     * Finds first collision within any pair of agents if it exists.
     * 
     * @return Conflict object if collision exists
     */
    public Optional<Conflict> findFirstConflict() {
        final var solutionEntries = this.solution.entrySet().stream().toList();
        Optional<Step> conflict;

        // loop through agent's pairs and their paths (i -> first agent, j -> second)
        for (int i = 0; i < solutionEntries.size(); i++) {
            for (int j = i + 1; j < solutionEntries.size(); j++) {

                // find conflict within 2 paths
                conflict = getFirstConflictWithin(
                        solutionEntries.get(i).getValue(),
                        solutionEntries.get(j).getValue());
                if (conflict.isPresent())
                    return Optional.of(new Conflict(
                            solutionEntries.get(i).getKey(),
                            solutionEntries.get(j).getKey(),
                            conflict.get()));
            }
        }
        return Optional.empty();
    }

    /**
     * Finds first collision through pair of paths if it exists
     * 
     * @param path1 first path
     * @param path2 second path
     * @return Step where collision exist OR empty Optional
     */
    private Optional<Step> getFirstConflictWithin(final Path path1, final Path path2) {
        for (final var step1 : path1.getPlan()) {
            for (final var step2 : path2.getPlan()) {
                if (step1.time() == step2.time() && step1.cell() == step2.cell())
                    return Optional.of(step1);
            }
        }

        return Optional.empty();
    }

    /**
     * Finds new paths for an agent, update current's node solution and cost
     * 
     * @param agent agent which path are refinding
     */
    public void updateSolution(final Agent agent) {
        this.updateSolution(List.of(agent));
    }

    /**
     * Finds new paths for collection of agents, update current's node solution and
     * cost
     * 
     * @param toUpdate collection of agents whose path are refinding
     */
    public void updateSolution(final List<Agent> toUpdate) {
        // finds new solution
        var newSolutions = CBSNode.findSolution(this.sspf, toUpdate, this.constraints);

        // update current solution
        for (var s : newSolutions.entrySet())
            this.solution.put(s.getKey(), s.getValue());

        // recalculate total cost of the node
        this.cost = costFunction.cost(solution.values());
    }

    /**
     * Finds independed paths for collection of agents
     * 
     * @param sspf        low-level algorithm of CBS used for independed pathfinding
     * @param toUpdate    list of agents whose paths finding is needed
     * @param constraints list of constraint for each agent implemented as pair of
     *                    (time, cell)
     * @return pairs of agents and theirs paths
     */
    public static Map<Agent, Path> findSolution(
            final SSPF sspf,
            List<Agent> toUpdate,
            Map<Agent, List<Step>> constraints) {
        final Map<Agent, Path> retval = new HashMap<>();
        var exit0 = sspf.getMap().getExits().get(0); // TODO: HARDCODED: implement pf to each exit and chosing the best
        Path path;
        for (final var agent : toUpdate) {
            path = sspf.findPath(agent.getPosition(), exit0, constraints.get(agent));
            retval.put(agent, path);
        }
        return retval;
    }

    // endregion

}
