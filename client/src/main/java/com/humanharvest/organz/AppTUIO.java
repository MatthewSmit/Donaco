package com.humanharvest.organz;

import java.io.IOException;
import java.util.Map;
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
import com.humanharvest.organz.utilities.view.PageNavigatorTouch;
import com.humanharvest.organz.utilities.view.TuioFXUtils;
import com.humanharvest.organz.utilities.view.WindowContext;
import com.sun.javafx.css.StyleManager;
import org.tuiofx.TuioFX;
import org.tuiofx.internal.base.TuioFXCanvas;

/**
 * The main class that runs the JavaFX GUI.
 */
public class AppTUIO extends Application {

    public static final Pane root = new TuioFXCanvas();

    public static void main(String[] args) {
        TuioFX.enableJavaFXTouchProperties();
        launch(args);
    }

    /**
     * Loads a backdrop page.
     */
    private static void loadBackPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane backPane = loader.load(PageNavigatorTouch.class.getResourceAsStream(Page.BACKDROP.getPath()));

        TuioFXUtils.setupPaneWithTouchFeatures(backPane);
        root.getChildren().add(backPane);
    }

    /**
     * Loads the landing page as the initial page.
     */
    private static void loadMainPane() {
        MainController mainController = PageNavigator.openNewWindow();
        mainController.setWindowContext(WindowContext.defaultContext());
        PageNavigator.loadPage(Page.LANDING, mainController);
    }

    /**
     * Starts the JavaFX GUI. Sets up the main stage and initialises the state of the system.
     * Loads from the save file or creates one if one does not yet exist.
     *
     * @param primaryStage The stage given by the JavaFX launcher.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        LoggerSetup.setup(Level.INFO);
        PageNavigator.setPageNavigator(new PageNavigatorTouch());

        Scene scene = new Scene(root, 1920, 1080);

        // Instead of tuioFX.enableMTWidgets(true)
        // We set our own stylesheet that contains less style changes but still loads the skins required for multi touch
        Application.setUserAgentStylesheet("MODENA");
        StyleManager.getInstance().addUserAgentStylesheet("/css/multifocus.css") ;

        loadBackPane();
        loadMainPane();

        scene.getStylesheets().add(AppUI.class.getResource("/css/touch.css").toExternalForm());

        primaryStage.setTitle("Test");
        primaryStage.setScene(scene);
        new MultitouchHandler(root);
//        primaryStage.setFullScreen(true);
        primaryStage.show();
        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);

        State.init(DataStorageType.REST);

        Map<String, String> parameters = getParameters().getNamed();

        if (parameters.containsKey("host")) {
            State.setBaseUri(parameters.get("host"));
        } else if (System.getenv("HOST") != null) {
            State.setBaseUri(System.getenv("HOST"));
        }
    }
}
