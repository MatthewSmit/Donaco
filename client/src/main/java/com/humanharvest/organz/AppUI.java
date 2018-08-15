package com.humanharvest.organz;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Level;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.DataStorageType;
import com.humanharvest.organz.utilities.LoggerSetup;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.PageNavigatorStandard;
import com.humanharvest.organz.utilities.view.PageNavigatorTouch;
import com.humanharvest.organz.utilities.view.TuioFXUtils;
import com.humanharvest.organz.utilities.view.WindowContext;
import com.sun.javafx.css.StyleManager;
import org.tuiofx.TuioFX;
import org.tuiofx.internal.base.TuioFXCanvas;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

/**
 * The main class that runs the JavaFX GUI.
 */
public class AppUI extends Application {

    public static void main(String[] args) {
        TuioFX.enableJavaFXTouchProperties();
        launch(args);
    }

    /**
     * Starts the JavaFX GUI. Sets up the main stage and initialises the state of the system.
     * Loads from the save file or creates one if one does not yet exist.
     * @param primaryStage The stage given by the JavaFX launcher.
     * @throws IOException If the save file cannot be loaded/created.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        LoggerSetup.setup(Level.INFO);

        processArguments();

        State.init(DataStorageType.REST);
        State.setPrimaryStage(primaryStage);

        Map<String, String> parameters = getParameters().getNamed();

        if (parameters.containsKey("host")) {
            State.BASE_URI = parameters.get("host");
        } else if (System.getenv("HOST") != null) {
            State.BASE_URI = System.getenv("HOST");
        }

        primaryStage.setTitle("Organ Client Management System");

        switch (State.getUiType()) {
            case STANDARD:
                primaryStage.setScene(createScene(loadStandardMainPane(primaryStage)));
                break;

            case TOUCH:
                Pane root = new TuioFXCanvas();
                Scene scene = new Scene(root);

                loadBackPane(root);
                MultitouchHandler.initialise(root);

                loadTouchMainPane();

                primaryStage.setScene(scene);

                //primaryStage.setFullScreen(true);
                break;

            default:
                throw new UnsupportedOperationException("Unknown ui type");
        }

        primaryStage.show();

        primaryStage.setMinHeight(639);
        primaryStage.setMinWidth(1016);
    }

    /**
     * Process the various command line and environmental arguments.
     */
    private void processArguments() {

        getArgument("host").ifPresent(State::setBaseUri);

        Optional<String> uiType = getArgument("ui");
        if (uiType.isPresent() && "touch".equalsIgnoreCase(uiType.get())) {
            State.setUiType(State.UiType.TOUCH);
            PageNavigator.setPageNavigator(new PageNavigatorTouch());

            // Instead of tuioFX.enableMTWidgets(true)
            // We set our own stylesheet that contains less style changes but still loads
            // the skins required for multi touch
            Application.setUserAgentStylesheet("MODENA");
            StyleManager.getInstance().addUserAgentStylesheet("/css/multifocus.css");
            StyleManager.getInstance().addUserAgentStylesheet("/css/touch.css");
        } else {
            State.setUiType(State.UiType.STANDARD);
            PageNavigator.setPageNavigator(new PageNavigatorStandard());
        }
    }

    /**
     * Returns an the value of an argument, or empty if non exist. Will do a case insensative comparison, and look in
     * both program arguments and environmental variables.
     */
    private Optional<String> getArgument(String argument) {
        Map<String, String> parameters = getParameters().getNamed();

        for (Entry<String, String> parameter : parameters.entrySet()) {
            if (parameter.getKey().equalsIgnoreCase(argument)) {
                return Optional.of(parameter.getValue());
            }
        }

        for (Entry<String, String> environment : System.getenv().entrySet()) {
            if (environment.getKey().equalsIgnoreCase(argument)) {
                return Optional.of(environment.getValue());
            }
        }

        return Optional.empty();
    }

    /**
     * Creates the main application scene.
     * @param mainPane The main application layout.
     * @return Returns the created scene.
     */
    private static Scene createScene(Pane mainPane) {
        Scene scene = new Scene(mainPane);
        addCss(scene);
        return scene;
    }

    public static void addCss(Scene scene) {
        scene.getStylesheets().add(AppUI.class.getResource("/css/validation.css").toExternalForm());
    }

    /**
     * Loads a backdrop page.
     */
    private static void loadBackPane(Pane rootPane) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane backPane = loader.load(PageNavigatorTouch.class.getResourceAsStream(Page.BACKDROP.getPath()));

        TuioFXUtils.setupPaneWithTouchFeatures(backPane);
        rootPane.getChildren().add(backPane);
    }

    /**
     * Loads the landing page as the initial page.
     */
    private static void loadTouchMainPane() {
        MainController mainController = PageNavigator.openNewWindow();
        mainController.setWindowContext(WindowContext.defaultContext());
        PageNavigator.loadPage(Page.LANDING, mainController);
    }

    /**
     * Loads the main FXML. Sets up the page-switching PageNavigator. Loads the landing page as the initial page.
     * @param stage The stage to set the window to
     * @return The loaded pane.
     * @throws IOException Thrown if the pane could not be loaded.
     */
    private Pane loadStandardMainPane(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Pane mainPane = loader.load(getClass().getResourceAsStream(Page.MAIN.getPath()));
        MainController mainController = loader.getController();
        mainController.setStage(stage);
        mainController.setPane(mainPane);
        mainController.setWindowContext(WindowContext.defaultContext());
        State.addMainController(mainController);
        PageNavigator.loadPage(Page.LANDING, mainController);

        return mainPane;
    }
}
