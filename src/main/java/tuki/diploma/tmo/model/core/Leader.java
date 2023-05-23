package tuki.diploma.tmo.model.core;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Leader extends Agent {

    public Leader(final Cell position) {
        super(position);
    }

    public Leader(final int x, final int y) {
        super(x, y);
    }

}
