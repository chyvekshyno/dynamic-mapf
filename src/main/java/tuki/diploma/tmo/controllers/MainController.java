package tuki.diploma.tmo.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Pair;
import tuki.diploma.tmo.controllers.dialogs.MapSizeController;
import tuki.diploma.tmo.controllers.handlers.ModeHandler.Mode;
import tuki.diploma.tmo.model.core.Environment;
import tuki.diploma.tmo.model.mapf.cbs.CBS;
import tuki.diploma.tmo.model.mapf.cbs.CBSCostFunction;
import tuki.diploma.tmo.model.mapf.sspf.SSPF;

public class MainController {

    private static String SER_MAP_DIR = "res/maps/";
    private static String SER_MAP_FILE = "latmap";

    private Stage primaryStage;

    private Environment environment;

    @FXML
    private EnvironmentController mapViewController;
    private PathFindingController pfController;
    private DynamicController dynamicController;

    @FXML
    Label labelControl;
    @FXML
    Label labelInfo;
    @FXML
    Button btnControlPlay;
    @FXML
    Button btnControlStop;
    @FXML
    Button btnControlStep;
    @FXML
    Button btnControlLoad;
    @FXML
    Button btnControlSave;
    @FXML
    Button btnControlNew;
    @FXML
    ToggleButton toggleControlDrawing;

    public MainController() {
        super();

        pfController = new PathFindingController();
        pfController.setMAPF(new CBS(CBSCostFunction.SIC, SSPF.AStar));

        dynamicController = new DynamicController();
    }

    @FXML
    public void initialize() {
        toggleControlDrawing
                .selectedProperty()
                .addListener((o, oldval, newval) -> mapViewController.setMode(newval ? Mode.DRAWING : Mode.NORMAL));

        dynamicController.setEnvironmentRedrawCallback(mapViewController);
    }

    // region METHODS

    public void setPrimaryStage(final Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setEnvironment(final Environment environment) {
        this.environment = environment;
        injectMapToContrlollers();
    }

    /**
     * Main function to finds agents paths and draw them on canvas view.
     */
    private void findPathsAndDraw() {
        pfController.findPathsMA();
        mapViewController.redraw();
    }

    // region CONTROLS HANDLING

    @FXML
    public void handlePlay(final ActionEvent event) {
        findPathsAndDraw();
        dynamicController.play();
    }

    @FXML
    public void handleStop(final ActionEvent event) {
        dynamicController.stop();
    }

    @FXML
    public void handleStep(final ActionEvent event) {
        dynamicController.step();
    }

    @FXML
    public void handleMapSaving() {
        serializeMap(mapViewController.getEnvironment());
    }

    @FXML
    public void handleMapLoading() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Serialized Object");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Serialized Object Files", "*.ser"));
        final File selectedFile = fileChooser.showOpenDialog(primaryStage);

        try {
            var loadedMap = loadMap(selectedFile);
            setEnvironment((Environment) loadedMap);
        } catch (ClassNotFoundException | IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleMapCreating(ActionEvent actionEvent) throws IOException {
        var url = getClass().getResource("map-size-dialog.fxml");
        FXMLLoader dialogLoader = new FXMLLoader(url);

        DialogPane dialogPane = dialogLoader.load();
        MapSizeController dialogController = dialogLoader.getController();

        Dialog<Pair<Integer, Integer>> dialog = new Dialog<>();
        dialog.setTitle("New Map Sizes");
        dialog.setDialogPane(dialogPane);

        dialogController.setDialogStage((Stage) dialog.getDialogPane().getScene().getWindow());

        dialog.showAndWait();

        dialogController.getSize()
                .ifPresent(size -> setEnvironment(new Environment(size.getKey(), size.getValue())));

        // Optional<Pair<Integer, Integer>> sizes = dialogController.getSize();
        // if (sizes.isPresent()) {
        // int height = sizes.get().getKey();
        // int width = sizes.get().getValue();
        // LatticeMap newMap = new LatticeMap(width, height);
        // setMap(newMap);
        // }
    }

    // endregion

    // region SERIALIZING LATTICE MAP

    private static void serializeMap(final Environment map) {
        final String projDir = System.getProperty("user.dir");
        final File folder = new File(projDir + '/' + SER_MAP_DIR);

        if (!folder.exists()) {
            if (folder.mkdirs()) {
                System.out.println("Created folder: " + folder.getAbsolutePath());
            } else {
                System.err.println("Failed to create folder: " + folder.getAbsolutePath());
            }
        }

        final String timestamp = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        final String filename = SER_MAP_DIR + SER_MAP_FILE + '-' + timestamp + ".ser";
        try (var out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(map);
        } catch (final IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static Environment loadMap(final File file)
            throws IOException, ClassNotFoundException {
        final var in = new ObjectInputStream(new FileInputStream(file));
        var map = (Environment) in.readObject();
        in.close();
        return map;
    }

    private void injectMapToContrlollers() {
        this.pfController.setMap(this.environment);
        this.mapViewController.setEnvironment(this.environment);
        this.dynamicController.setEnvironment(this.environment);
    }

    // endregion
    // endregion
}
