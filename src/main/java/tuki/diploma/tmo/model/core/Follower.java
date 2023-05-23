package tuki.diploma.tmo.model.core;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class Follower extends Agent{

    private Leader leader;

    public Follower(Cell position) {
        super(position);
    }

    public Follower(int x, int y) {
        super(x, y);
    }

    public Follower(Cell position, Leader leader) {
        super(position);
        this.leader = leader;
    }

    public Follower(int x, int y, Leader leader) {
        super(x, y);
        this.leader = leader;
    }
}
