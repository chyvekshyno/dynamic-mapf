package tuki.diploma.tmo.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import tuki.diploma.tmo.model.core.Agent;
import tuki.diploma.tmo.model.core.Environment;

public class DynamicController {

    private Environment environment;
    private Integer timeStep;
    private final Timeline timeline;

    EnvironmentRedrawCallback redrawCallback;

    public DynamicController() {
        this.timeStep = 0;
        this.timeline = new Timeline(new KeyFrame(
                Duration.millis(500),
                event -> stepAction()));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.timeline.stop();
    }

    public DynamicController(Environment environment) {
        this();
        this.environment = environment;
    }

    private void stepAction() {
        environment.getAgents().forEach(Agent::pathStep);
        redrawCallback.redraw();
        timeStep++;
    }

    public void setEnvironmentRedrawCallback(EnvironmentRedrawCallback callback) {
        this.redrawCallback = callback;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Integer getTimeStep() {
        return timeStep;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void stop() {
        timeline.stop();
    }

    public void step() {
        stepAction();
        timeline.setCycleCount(timeline.getCycleCount() + 1);
    }

    public void play() {
        timeline.play();
    }

}
