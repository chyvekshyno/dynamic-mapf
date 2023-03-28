module tuki.diploma.tmo {
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;

    requires lombok;

    opens tuki.diploma.tmo to javafx.fxml;
    exports tuki.diploma.tmo;

    opens tuki.diploma.tmo.controllers to javafx.fxml;
    exports tuki.diploma.tmo.controllers;
}
