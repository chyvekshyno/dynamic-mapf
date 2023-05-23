package tuki.diploma.tmo.controllers;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import tuki.diploma.tmo.controllers.handlers.ModeHandler;
import tuki.diploma.tmo.model.ca.FireCell;
import tuki.diploma.tmo.model.core.Agent;
import tuki.diploma.tmo.model.core.Cell;
import tuki.diploma.tmo.model.core.Coordinate;
import tuki.diploma.tmo.model.core.Environment;
import tuki.diploma.tmo.model.core.Follower;
import tuki.diploma.tmo.model.core.Leader;
import tuki.diploma.tmo.model.core.Path;

public class EnvironmentController implements ModeHandler, EnvironmentRedrawCallback {

    private enum DrawingColors {
        CELL(Color.LIGHTGRAY),
        FOLLOWER(Color.CORNFLOWERBLUE),
        FOLLOWER_PATH(Color.CORNFLOWERBLUE),
        LEADER(Color.BLUEVIOLET),
        LEADER_PATH(Color.BLUEVIOLET),
        EXIT(Color.GREEN),
        NON_WALKABLE(Color.BLACK);

        final private Color color;

        private DrawingColors(final Color color) {
            this.color = color;
        }

        public Color color() {
            return color;
        }

    }

    private static int SCALE_INIT = 25;
    private static double SCALE_FACTOR = 1.1;

    private double sceneMouseX;
    private double sceneMouseY;

    private Environment environment;
    private Mode mode = Mode.NORMAL;

    @FXML
    public ScrollPane scrollPaneCanvas;
    @FXML
    public Group groupCanvas;
    @FXML
    private Canvas canvasDrawing;

    @FXML
    public void initialize() {
        scrollPaneCanvas.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            this.sceneMouseX = event.getX();
            this.sceneMouseY = event.getY();
        });
        scrollPaneCanvas.setPannable(true);
        scrollPaneCanvas.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case PLUS, EQUALS, ADD -> scaleCanvas(sceneMouseX, sceneMouseY, true);
                    case MINUS -> scaleCanvas(sceneMouseX, sceneMouseY, false);
                    default -> {
                    }
                }
                event.consume();
            }
        });
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public void setMode(final Mode mode) {
        this.mode = mode;
    }

    // region METHODS

    public void clear() {
        final GraphicsContext gc = canvasDrawing.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasDrawing.getWidth(), canvasDrawing.getHeight());
    }

    @Override
    public void redraw() {
        clear();
        drawLatticeMap();
        drawAgents();
        // drawCellsNeighborsDirections();
        drawAgentsPaths();
    }

    /**
     * Draws regular lattice map on canvas includding non-walkable cells and exits
     */
    public void drawLatticeMap() {
        canvasDrawing.setWidth(environment.getWidth() * SCALE_INIT);
        canvasDrawing.setHeight(environment.getHeight() * SCALE_INIT);

        final GraphicsContext gc = canvasDrawing.getGraphicsContext2D();

        final var grid = environment.getGrid();
        final var exits = environment.getExits();

        Cell cell;
        Color fillColor;
        for (int col = 0; col < environment.getWidth(); col++) {
            for (int row = 0; row < environment.getHeight(); row++) {
                cell = grid.get(col).get(row);

                if (!cell.isWalkable()) {
                    fillColor = DrawingColors.NON_WALKABLE.color();
                } else if (exits.contains(cell)) {
                    fillColor = DrawingColors.EXIT.color();
                } else {
                    fillColor = DrawingColors.CELL.color();
                }

                gc.setFill(fillColor);
                gc.fillRect(col * SCALE_INIT, row * SCALE_INIT, SCALE_INIT - 2, SCALE_INIT - 2);
            }
        }
    }

    /**
     * Draws list of agents (followers + leaders) and theirs paths on agent's canvas
     */
    public void drawAgents() {
        final List<Agent> agentList = environment.getAgents();
        final GraphicsContext gc = canvasDrawing.getGraphicsContext2D();

        Color agentColor;
        Coordinate agentPosition;
        for (final var agent : agentList) {
            agentColor = agent instanceof Leader
                    ? DrawingColors.LEADER.color()
                    : DrawingColors.FOLLOWER.color();

            agentPosition = agent.getPosition().getCoord();

            gc.setFill(agentColor);
            gc.fillOval(
                    agentPosition.x() * SCALE_INIT + 1,
                    agentPosition.y() * SCALE_INIT + 1,
                    SCALE_INIT - 3, SCALE_INIT - 3);
        }
    }

    public void drawAgentsPaths() {
        environment.getAgents().forEach(this::drawAgentPath);
    }

    public void drawAgentPath(final Agent agent) {
        final Path path = agent.getPath();
        if (path == null || path.isEmpty())
            return;

        final GraphicsContext gc = canvasDrawing.getGraphicsContext2D();

        gc.setLineWidth(1.5);
        gc.setFill(agent instanceof Leader
                ? DrawingColors.LEADER_PATH.color()
                : DrawingColors.FOLLOWER.color());

        Cell prevCell = agent.getPosition();
        for (var cell : path.getPath()) {
            drawLine(prevCell.getCoord(), cell.getCoord(), gc);
            prevCell = cell;
        }
    }

    // region TEST: DRAWING CELL's NEIGHBORHOODS

    private void drawCellsNeighborsDirections() {
        final var grid = environment.getGrid();
        FireCell cell;
        for (int col = 0; col < environment.getWidth(); col++) {
            for (int row = 0; row < environment.getHeight(); row++) {
                cell = grid.get(col).get(row);
                drawCellNeighborsDirections(cell);
            }
        }
    }

    private void drawCellNeighborsDirections(final FireCell cell) {
        final var gc = canvasDrawing.getGraphicsContext2D();
        gc.setLineWidth(0.5);
        gc.setFill(Color.DARKGRAY);

        environment.getNeighbors(cell)
                .stream()
                .map(Cell::getCoord)
                .forEach(coord -> drawLine(coord, cell.getCoord(), gc));
    }

    // endregion

    private void drawLine(
            final Coordinate coord1,
            final Coordinate coord2,
            final GraphicsContext gc) {
        gc.strokeLine(
                SCALE_INIT * (coord1.x() + 0.5),
                SCALE_INIT * (coord1.y() + 0.5),
                SCALE_INIT * (coord2.x() + 0.5),
                SCALE_INIT * (coord2.y() + 0.5));
    }

    public Environment getEnvironment() { // MapHandler interface
        return this.environment;
    }

    public void setEnvironment(final Environment map) { // MapHandler interface
        this.environment = map;
        redraw();
    }

    /**
     * Scale canvases in/out
     * 
     * @param x       mouse position on `x` coordinate
     * @param y       mouse position on `y` coordinate
     * @param scaleIn defines direction of scaling
     * @param scale   absolute number of scale factor
     */
    private void scaleCanvas(final double x, final double y, final boolean scaleIn, final double scale) {
        final double scaleFactor = scaleIn ? SCALE_FACTOR : 1 / SCALE_FACTOR;

        final double canvasTranslateX = groupCanvas.getTranslateX();
        final double canvasTranslateY = groupCanvas.getTranslateY();

        final double newTranslateX = canvasTranslateX - (scaleFactor - 1) * (x - canvasTranslateX);
        final double newTranslateY = canvasTranslateY - (scaleFactor - 1) * (x - canvasTranslateY);

        groupCanvas.setScaleX(groupCanvas.getScaleX() * scaleFactor);
        groupCanvas.setScaleY(groupCanvas.getScaleY() * scaleFactor);
        groupCanvas.setTranslateX(newTranslateX);
        groupCanvas.setTranslateY(newTranslateY);
    }

    /**
     * Scale canvases in/out using default scale factor
     * 
     * @param x       mouse position on `x` coordinate
     * @param y       mouse position on `y` coordinate
     * @param scaleIn defines direction of scaling
     */
    private void scaleCanvas(final double x, final double y, final boolean scaleIn) {
        scaleCanvas(x, y, scaleIn, SCALE_FACTOR);
    }

    // endregion

    // region HANDLE CONTROLS

    public void handleMouseEvent(final MouseEvent event) {
        if (mode == Mode.DRAWING) {
            handleDrawing(event);
        }
    }

    private void handleDrawing(final MouseEvent event) {
        final int col = (int) (event.getX() / SCALE_INIT);
        final int row = (int) (event.getY() / SCALE_INIT);

        final Cell cell = environment.getCell(col, row);

        if (event.isControlDown()) { // CTRL + ...
            switch (event.getButton()) {
                case PRIMARY -> toggleAgent(environment, cell);
                case SECONDARY -> toggleExit(environment, cell);
                default -> {
                    break;
                }
            }
        } else {
            switch (event.getButton()) {
                case PRIMARY -> toggleWall(environment, cell);
                default -> {
                    break;
                }
            }
        }

        redraw();
    }

    private void toggleWall(final Environment map, final Cell cell) {
        cell.setWalkable(!cell.isWalkable());
    }

    private void toggleAgent(final Environment map, final Cell cell) {
        final var agents = map.getAgents();
        agents.stream()
                .filter(a -> a.getPosition().atSameCoord(cell))
                .findFirst().ifPresentOrElse(
                        a -> {
                            if (a instanceof final Follower f) {
                                map.becomeLeader(f);
                            } else if (a instanceof final Leader l) {
                                map.removeAgent(l);
                            } else
                                throw new ClassCastException("Unknown class isntance of Agent in agent list.");
                        },
                        () -> map.addAgent(new Follower(
                                cell.getCoord().x(),
                                cell.getCoord().y())));
    }

    private void toggleExit(final Environment map, final Cell cell) {
        if (map.getExits().contains(cell)) {
            map.removeExit(cell);
        } else {
            map.addExit(cell);
        }
    }

    // endregion

}
