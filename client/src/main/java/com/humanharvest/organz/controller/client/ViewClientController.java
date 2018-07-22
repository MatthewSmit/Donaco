package com.humanharvest.organz.controller.client;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.resolvers.client.MarkClientAsDeadResolver;
import com.humanharvest.organz.resolvers.client.ModifyClientDetailsResolver;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.ui.validation.UIValidation;
import com.humanharvest.organz.utilities.JSONConverter;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.validators.IntValidator;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.views.client.ModifyClientObject;
import org.controlsfx.control.Notifications;

/**
 * Controller for the view/edit client page.
 */
public class ViewClientController extends SubController {

    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy\nh:mm:ss a");
    private static final Logger LOGGER = Logger.getLogger(ViewClientController.class.getName());

    private final Session session;
    private final ClientManager manager;
    private Client viewedClient;

    @FXML
    private Pane sidebarPane;
    @FXML
    private Pane imagePane;
    @FXML
    private Pane inputsPane;
    @FXML
    private Pane menuBarPane;
    @FXML
    private Label creationDate;
    @FXML
    private Label lastModified;
    @FXML
    private Label fnameLabel;
    @FXML
    private Label lnameLabel;
    @FXML
    private Label dobLabel;
    @FXML
    private Label dodLabel;
    @FXML
    private Label heightLabel;
    @FXML
    private Label weightLabel;
    @FXML
    private Label ageDisplayLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label bmiLabel;
    @FXML
    private Label fullName;
    @FXML
    private TextField fname;
    @FXML
    private TextField lname;
    @FXML
    private TextField mname;
    @FXML
    private TextField pname;
    @FXML
    private TextField height;
    @FXML
    private TextField weight;
    @FXML
    private TextField address;
    @FXML
    private DatePicker dob;
    @FXML
    private DatePicker dod;
    @FXML
    private ChoiceBox<Gender> gender;
    @FXML
    private ChoiceBox<Gender> genderIdentity;
    @FXML
    private ChoiceBox<BloodType> btype;
    @FXML
    private ChoiceBox<Region> region;
    @FXML
    private ImageView imageView;

    public ViewClientController() {
        manager = State.getClientManager();
        session = State.getSession();
    }

    /**
     * Initializes the UI for this page.
     * - Loads the sidebar.
     * - Adds all values to the gender, genderIdentity, blood type, and region dropdown lists.
     * - Disables all fields.
     * - If a client is logged in, populates with their info and removes ability to view a different client.
     * - If the viewUserId is set, populates with their info.
     */
    @FXML
    private void initialize() {
        gender.setItems(FXCollections.observableArrayList(Gender.values()));
        genderIdentity.setItems(FXCollections.observableArrayList(Gender.values()));
        btype.setItems(FXCollections.observableArrayList(BloodType.values()));
        region.setItems(FXCollections.observableArrayList(Region.values()));
        fullName.setWrapText(true);
    }

    private void loadImage() {
        try {
            Image image = new Image("https://img.rl0.ru/f3f70ad4661bcac56b285cb86efdea1e/c615x400/news.rambler.ru/img/2018/05/23142637.159422.8999.jpg");
//            Image image = new Image("./../../../../../../resources/images/harold.jpg");
            imageView.setImage(image);
            imageView.setFitHeight(150);
            imageView.setFitWidth(150);
            imageView.setPreserveRatio(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        if (session.getLoggedInUserType() == Session.UserType.CLIENT) {
            viewedClient = session.getLoggedInClient();
            mainController.loadSidebar(sidebarPane);
        } else if (windowContext.isClinViewClientWindow()) {
            viewedClient = windowContext.getViewClient();
            mainController.loadMenuBar(menuBarPane);
        }
        loadImage();
        refresh();
    }

    @Override
    public void refresh() {
        try {
            viewedClient = manager.getClientByID(viewedClient.getUid()).orElseThrow(ServerRestException::new);
        } catch (ServerRestException e) {
            e.printStackTrace();
            PageNavigator.showAlert(AlertType.ERROR,
                    "Server Error",
                    "An error occurred while trying to fetch from the server.\nPlease try again later.");
        }
        updateClientFields();
        if (session.getLoggedInUserType() == UserType.CLIENT) {
            mainController.setTitle("View Client: " + viewedClient.getPreferredName());
        } else if (windowContext.isClinViewClientWindow()) {
            mainController.setTitle("View Client: " + viewedClient.getFullName());
        }
    }

    private void updateClientFields() {
        fname.setText(viewedClient.getFirstName());
        lname.setText(viewedClient.getLastName());
        mname.setText(viewedClient.getMiddleName());
        pname.setText(viewedClient.getPreferredNameOnly());
        dob.setValue(viewedClient.getDateOfBirth());
        dod.setValue(viewedClient.getDateOfDeath());
        gender.setValue(viewedClient.getGender());
        genderIdentity.setValue(viewedClient.getGenderIdentity());
        height.setText(String.valueOf(viewedClient.getHeight()));
        weight.setText(String.valueOf(viewedClient.getWeight()));
        btype.setValue(viewedClient.getBloodType());
        region.setValue(viewedClient.getRegion());
        address.setText(viewedClient.getCurrentAddress());
        fullName.setText(viewedClient.getFullName());

        creationDate.setText(viewedClient.getCreatedTimestamp().format(dateTimeFormat));
        if (viewedClient.getModifiedTimestamp() == null) {
            lastModified.setText("User has not been modified yet.");
        } else {
            lastModified.setText(viewedClient.getModifiedTimestamp().format(dateTimeFormat));
        }

        displayBMI();
        displayAge();

    }


    /**
     * Saves the changes a user makes to the viewed client if all their inputs are valid.
     * Otherwise the invalid fields text turns red.
     */
    @FXML
    private void apply() {
        if (checkMandatoryFields() && checkNonMandatoryFields()) {
            if (updateChanges()) {
                displayBMI();
                displayAge();
                lastModified.setText(viewedClient.getModifiedTimestamp().format(dateTimeFormat));
            }
        }
    }

    /**
     * Resets the page back to its default state.
     */
    @FXML
    private void cancel() {
        refresh();
    }

    /**
     * Checks that all mandatory fields have valid arguments inside. Otherwise display red text on the invalidly entered
     * labels.
     * @return true if all mandatory fields have valid input.
     */
    private boolean checkMandatoryFields() {
        boolean update = true;
        if (fname.getText().isEmpty()) {
            fnameLabel.setTextFill(Color.RED);
            update = false;
        } else {
            fnameLabel.setTextFill(Color.BLACK);
        }

        if (lname.getText().isEmpty()) {
            lnameLabel.setTextFill(Color.RED);
            update = false;
        } else {
            lnameLabel.setTextFill(Color.BLACK);
        }
        if (dob.getValue() == null || dob.getValue().isAfter(LocalDate.now())) {
            dobLabel.setTextFill(Color.RED);
            update = false;
        } else {
            dobLabel.setTextFill(Color.BLACK);
        }
        return update;
    }

    /**
     * Checks that non mandatory fields have either valid input, or no input. Otherwise red text is shown.
     * @return true if all non mandatory fields have valid/no input.
     */
    private boolean checkNonMandatoryFields() {
        boolean update = true;
        if (dod.getValue() == null ||
                (dod.getValue().isAfter(dob.getValue())) && dod.getValue().isBefore(LocalDate.now().plusDays(1))) {
            dodLabel.setTextFill(Color.BLACK);
        } else {
            dodLabel.setTextFill(Color.RED);
            update = false;
        }

        try {
            double h = Double.parseDouble(height.getText());
            if (h < 0) {
                heightLabel.setTextFill(Color.RED);
                update = false;
            } else {
                heightLabel.setTextFill(Color.BLACK);
            }

        } catch (NumberFormatException ex) {
            heightLabel.setTextFill(Color.RED);
        }

        try {
            double w = Double.parseDouble(weight.getText());
            if (w < 0) {
                weightLabel.setTextFill(Color.RED);
                update = false;
            } else {
                weightLabel.setTextFill(Color.BLACK);
            }

        } catch (NumberFormatException ex) {
            weightLabel.setTextFill(Color.RED);
            update = false;
        }
        return update;
    }

    private void addChangeIfDifferent(ModifyClientObject modifyClientObject, String fieldString, Object newValue) {
        try {
            //Get the field from the string
            Field field = modifyClientObject.getClass().getDeclaredField(fieldString);
            Field clientField = viewedClient.getClass().getDeclaredField(fieldString);
            //Allow access to any fields including private
            field.setAccessible(true);
            clientField.setAccessible(true);
            //Only add the field if it differs from the client
            if (!Objects.equals(clientField.get(viewedClient), newValue)) {
                field.set(modifyClientObject, newValue);
                modifyClientObject.registerChange(fieldString);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Records the changes updated as a ModifyClientAction to trace the change in record.
     * @return If there were any changes made
     */
    private boolean updateChanges() {
        ModifyClientObject modifyClientObject = new ModifyClientObject();

        ModifyClientDetailsResolver resolver = new ModifyClientDetailsResolver(viewedClient, modifyClientObject);

        boolean clientDied = false;

        if (viewedClient.getDateOfDeath() == null && dod.getValue() != null) {
            Optional<ButtonType> buttonOpt = PageNavigator.showAlert(AlertType.CONFIRMATION,
                    "Are you sure you want to mark this client as dead?",
                    "This will cancel all waiting transplant requests for this client.");

            if (buttonOpt.isPresent() && buttonOpt.get() == ButtonType.OK) {

                MarkClientAsDeadResolver deadResolver = new MarkClientAsDeadResolver(viewedClient, dod.getValue());
                try {
                    deadResolver.execute();
                } catch (NotFoundException e) {
                    LOGGER.log(Level.WARNING, "Client not found");
                    PageNavigator.showAlert(
                            AlertType.WARNING,
                            "Client not found",
                            "The client could not be found on the server, it may have been deleted");
                    return false;
                } catch (ServerRestException e) {
                    LOGGER.log(Level.WARNING, e.getMessage(), e);
                    PageNavigator.showAlert(
                            AlertType.WARNING,
                            "Server error",
                            "Could not apply changes on the server, please try again later");
                    return false;
                } catch (IfMatchFailedException e) {
                    LOGGER.log(Level.INFO, "If-Match did not match");
                    PageNavigator.showAlert(
                            AlertType.WARNING,
                            "Outdated Data",
                            "The client has been modified since you retrieved the data.\n"
                                    + "If you would still like to apply these changes please submit again, "
                                    + "otherwise refresh the page to update the data.");
                    return false;
                }
                clientDied = true;

                Notifications.create()
                        .title("Marked Client as Dead")
                        .text("All organ transplant requests have been removed")
                        .showConfirm();
            }
        } else {
            addChangeIfDifferent(modifyClientObject, "dateOfDeath", dod.getValue());
        }

        addChangeIfDifferent(modifyClientObject, "firstName", fname.getText());
        addChangeIfDifferent(modifyClientObject, "lastName", lname.getText());
        addChangeIfDifferent(modifyClientObject, "middleName", mname.getText());
        addChangeIfDifferent(modifyClientObject, "preferredName", pname.getText());
        addChangeIfDifferent(modifyClientObject, "dateOfBirth", dob.getValue());
        addChangeIfDifferent(modifyClientObject, "gender", gender.getValue());
        addChangeIfDifferent(modifyClientObject, "genderIdentity", genderIdentity.getValue());
        addChangeIfDifferent(modifyClientObject, "height", Double.parseDouble(height.getText()));
        addChangeIfDifferent(modifyClientObject, "weight", Double.parseDouble(weight.getText()));
        addChangeIfDifferent(modifyClientObject, "bloodType", btype.getValue());
        addChangeIfDifferent(modifyClientObject, "region", region.getValue());
        addChangeIfDifferent(modifyClientObject, "currentAddress", address.getText());

        if (modifyClientObject.getModifiedFields().isEmpty()) {
            if (!clientDied) {
                Notifications.create()
                        .title("No changes were made.")
                        .text("No changes were made to the client.")
                        .showWarning();
                return false;
            }
        }

        try {
            resolver.execute();
            String actionText = modifyClientObject.toString();

            Notifications.create()
                    .title("Updated Client")
                    .text(actionText)
                    .showInformation();

            HistoryItem save = new HistoryItem("UPDATE CLIENT INFO",
                    String.format("Updated client %s with values: %s", viewedClient.getFullName(), actionText));
            JSONConverter.updateHistory(save, "action_history.json");

        } catch (NotFoundException e) {
            LOGGER.log(Level.WARNING, "Client not found");
            PageNavigator.showAlert(AlertType.WARNING, "Client not found", "The client could not be found on the "
                    + "server, it may have been deleted");
            return false;
        } catch (ServerRestException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            PageNavigator.showAlert(AlertType.WARNING, "Server error", "Could not apply changes on the server, "
                    + "please try again later");
            return false;
        } catch (IfMatchFailedException e) {
            LOGGER.log(Level.INFO, "If-Match did not match");
            PageNavigator.showAlert(
                    AlertType.WARNING,
                    "Outdated Data",
                    "The client has been modified since you retrieved the data.\nIf you would still like to "
                    + "apply these changes please submit again, otherwise refresh the page to update the data.");
            return false;
        }

        PageNavigator.refreshAllWindows();
        return true;

    }

    /**
     * Displays the currently viewed clients BMI.
     */
    private void displayBMI() {
        bmiLabel.setText(String.format("%.01f", viewedClient.getBMI()));
    }

    /**
     * Displays either the current age, or age at death of the client depending on if the date of death field has been
     * filled in.
     */
    private void displayAge() {
        if (viewedClient.getDateOfDeath() == null) {
            ageDisplayLabel.setText("Age:");
        } else {
            ageDisplayLabel.setText("Age at death:");
        }
        ageLabel.setText(String.valueOf(viewedClient.getAge()));
    }
}
