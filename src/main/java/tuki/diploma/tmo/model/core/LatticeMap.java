package tuki.diploma.tmo.model.core;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class LatticeMap {
    private final int width;
    private final int height;
    private final Cell[][] grid;
    private final List<Agent> agents;
    private final List<Cell> exits;

    public LatticeMap(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.grid = new Cell[width][height];
        this.agents = new ArrayList<>();
        this.exits = new ArrayList<>();

        initMap();
    }

    private void initMap() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.grid[x][y] = new Cell(x, y);
            }
        }
    }

    // region Methods

    public List<Cell> getNeighbors(int x, int y) {
        List<Cell> neighbors = new ArrayList<>();

        int[] dx = { -1, 0, 1, 1, 1, 0, -1, -1 };
        int[] dy = { -1, -1, -1, 0, 1, 1, 1, 0 };

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];

            if (isValidPosition(newX, newY) && !grid[newX][newY].isWalkable()) {
                if (i < 4 || (!grid[x + dx[i - 4]][y].isWalkable() && !grid[x][y + dy[i - 4]].isWalkable())) {
                    neighbors.add(grid[newX][newY]);
                }
            }
        }

        return neighbors;
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width 
            && y >= 0 && y < height;
    }

    // endregion

}
