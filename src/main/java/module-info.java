module tuki.diploma.tmo {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires transitive javafx.fxml;

    opens tuki.diploma.tmo to javafx.fxml;
    exports tuki.diploma.tmo;

    opens tuki.diploma.tmo.controllers to javafx.fxml;
    exports tuki.diploma.tmo.controllers;
}
