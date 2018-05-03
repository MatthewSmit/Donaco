package seng302.Controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import org.junit.Test;

import seng302.Client;
import seng302.Clinician;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;
import seng302.Utilities.View.WindowContext.WindowContextBuilder;

import org.junit.Test;

public class SidebarControllerClinicianTest extends ControllerTest{

    private Clinician testClinician = new Clinician("Mr", null, "Tester",
            "9 Fake St", Region.AUCKLAND, 3, "k");
    private Client client = new Client("Client", "Number", "One", LocalDate.now(), 1);


    @Override
    protected Page getPage() {
        return Page.SIDEBAR;
    }

    @Override
    protected void initState() {
        State.init();
        State.getClinicianManager().addClinician(testClinician);
        State.getClientManager().getClientByID(1);
        State.login(testClinician);
        mainController.setWindowContext(new WindowContextBuilder().setAsClinViewClientWindow().viewClient(client).build());
    }

    @Test
    public void testClickOnUndo() {
        clickOn("#undoButton");
    }

    @Test
    public void testClickOnRedo() {
        clickOn("#redoButton");
    }


    @Test
    public void testClickOnViewClient() {
        clickOn("#viewClientButton");
        assertEquals(Page.VIEW_CLIENT, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnRegisterOrgans() {
        clickOn("registerOrganDonationButton");
        assertEquals(Page.REGISTER_ORGAN_DONATIONS, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnRequestOrgans() {
        clickOn("#requestOrganDonationButton");
        assertEquals(Page.REQUEST_ORGANS, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnViewMedications() {
        clickOn("#viewMedicationsButton");
        assertEquals(Page.VIEW_MEDICATIONS, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnMedicalConditions() {
        clickOn("#illnessHistoryButton");
        assertEquals(Page.VIEW_MEDICAL_HISTORY, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnViewClinician() {
        clickOn("#viewClinicianButton");
        assertEquals(Page.VIEW_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnSearch() {
        clickOn("#searchButton");
        assertEquals(Page.SEARCH, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnTransplants() {
        clickOn("#transplantsButton");
        assertEquals(Page.TRANSPLANTS, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnHistory() {
        clickOn("#viewHistoryButton");
        assertEquals(Page.HISTORY, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnSaveAllClients() {
        clickOn("#saveAllClientsButton");
    }

    @Test
    public void testClickOnLoadClients() {
        clickOn("#loadClientsButton");
    }

    @Test
    public void testClickOnLogout() {
        clickOn("#logoutButton");
        assertEquals(Page.LANDING, mainController.getCurrentPage());
    }


}