package tuki.diploma.tmo.model.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

import lombok.Data;

@Data
public class Path {
    private final PriorityQueue<Step> plan; // TODO: rework with TreeSet instead of PriorityQueue
    private Double cost;

    public Path(final PriorityQueue<Step> plan) {
        this.plan = plan;
        calcCost();
    }

    public Path(final PriorityQueue<Step> plan, final Double cost) {
        this.plan = plan;
        this.cost = cost;
    }

    private void calcCost() {
        double newCost = 0.0;
        for (final var step : plan) {
            newCost += step.time();
        }
        this.cost = newCost;
    }

    public int size() {
        return plan.size();
    }

    public Optional<Cell> get(final int timestep) {
        for (final var step : plan) {
            if (step.time() == timestep)
                return Optional.of(step.cell());

            if (step.time() > timestep)
                break;
        }
        return Optional.empty();
    }

    public List<Step> findConflictSteps(final Path path) {
        // TODO: loop through shorter path
        List<Step> retval = new ArrayList<>();

        var it2 = path.getPlan().iterator();
        var step2 = it2.next();
        int timestep = 0;
        for (var step1 : this.plan) {
            timestep = step1.time();

            while (step2.time() < timestep)
                step2 = it2.next();

            if (step2.time() != timestep)
                continue;

            if (step1.cell() == step2.cell()) 
                retval.add(step1);
        }
        return retval;
    }

    public boolean isEmpty() {
        return plan.isEmpty();
    }

    public List<Cell> getPath() {
        return plan.stream()
                .sorted()
                .map(Step::cell)
                .toList();
    }

    public Cell step() {
        return plan.poll().cell();
    }

    public static Path empty() {
        return new Path(new PriorityQueue<>(), 0.);
    }
}
