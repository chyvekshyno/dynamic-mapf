package tuki.diploma.tmo.controllers.handlers;

public interface ModeHandler {

    public enum Mode {
        NORMAL,
        DRAWING,
        PROCESSING,
    }

    Mode getMode();
    void setMode(Mode mode);
}
