package tuki.diploma.tmo.model.core;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Follower extends Agent{

    private Leader leader;

    public Follower(int id, Cell position) {
        super(id, position);
    }

    public Follower(int id, int x, int y) {
        super(id, x, y);
    }

}
