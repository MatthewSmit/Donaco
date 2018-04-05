package seng302.Controller.Donor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.Donor;
import seng302.MedicationHistoryItem;
import seng302.State.Session;
import seng302.State.State;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import org.controlsfx.control.ListSelectionView;

public class ViewMedicationsController extends SubController {

    private Session session;
    private Donor donor;

    private ObservableList<MedicationHistoryItem> pastMedications = FXCollections.observableArrayList();
    private ObservableList<MedicationHistoryItem> currentMedications = FXCollections.observableArrayList();

    @FXML
    private Pane sidebarPane;

    @FXML
    private TextField newMedField;

    @FXML
    private ListSelectionView<MedicationHistoryItem> medicationListsView;

    public ViewMedicationsController() {
        session = State.getSession();
    }

    @FXML
    private void initialize() {
        medicationListsView.setSourceHeader(new Label("Past Medications"));
        medicationListsView.setTargetHeader(new Label("Current Medications"));

        new AutoCompletionTextFieldBinding<>(newMedField, param -> getSuggestions(newMedField.getText()));

        pastMedications.addListener((ListChangeListener<MedicationHistoryItem>) change -> {
            change.next();
            for (MedicationHistoryItem item : change.getAddedSubList()) {
                item.setStopped(LocalDate.now());
            }
        });

        currentMedications.addListener((ListChangeListener<MedicationHistoryItem>) change -> {
            change.next();
            for (MedicationHistoryItem item : change.getAddedSubList()) {
                item.setStarted(LocalDate.now());
                item.setStopped(null);
            }
        });
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadSidebar(sidebarPane);

        if (session.getLoggedInUserType() == Session.UserType.DONOR) {
            donor = session.getLoggedInDonor();
        } else if (windowContext.isClinViewDonorWindow()) {
            donor = windowContext.getViewDonor();
        }

        medicationListsView.setSourceItems(pastMedications);
        medicationListsView.setTargetItems(currentMedications);
        refreshMedicationLists();
    }

    @FXML
    public void newMedKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            addMedication(newMedField.getText());
        }
    }

    private void refreshMedicationLists() {
        pastMedications.setAll(donor.getPastMedications());
        currentMedications.setAll(donor.getCurrentMedications());
    }

    private void addMedication(String newMedName) {
        MedicationHistoryItem newMed = new MedicationHistoryItem(newMedName, LocalDate.now(), null);
        donor.getCurrentMedications().add(newMed);

        newMedField.setText("");
        refreshMedicationLists();
    }

    private List<String> getSuggestions(String input) {
        return new ArrayList<>();
    }
}
