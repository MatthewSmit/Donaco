package com.humanharvest.organz.controller.client;

import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;

import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext.WindowContextBuilder;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class SidebarControllerClientReceiverTest extends ControllerTest {

    private Client client = new Client("Client", "Number", "One", LocalDate.now(), 1);


    @Override
    protected Page getPage() {
        return Page.SIDEBAR;
    }

    @Override
    protected void initState() {
        client.addTransplantRequest(new TransplantRequest(client, Organ.LIVER));
        State.reset(false);
        State.login(client);
        State.getClientManager().addClient(client);
        mainController.setWindowContext(new WindowContextBuilder().build());
    }

    // Test clicking on action buttons

    @Test
    public void testClickOnUndo() {
        clickOn("#undoButton");
    }

    @Test
    public void testClickOnRedo() {
        clickOn("#redoButton");
    }

    // Test clicking on buttons to go to another screen

    @Test
    public void testClickOnViewClient() {
        clickOn("#viewClientButton");
        assertEquals(Page.VIEW_CLIENT, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnRegisterOrgans() {
        clickOn("#registerOrganDonationButton");
        assertEquals(Page.REGISTER_ORGAN_DONATIONS, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnRequestOrgansWithOrgansRequested() {
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
    public void testClickOnHistory() {
        clickOn("Action history");
        assertEquals(Page.HISTORY, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnLogout() {
        clickOn("#logoutButton");
        assertEquals(Page.LANDING, mainController.getCurrentPage());
    }

    // Test admin-only buttons are hidden

    @Test
    public void testCreateAdminHidden() {
        verifyThat("#createAdminButton", isInvisible());
    }

    @Test
    public void testCreateClinicianHidden() {
        verifyThat("#createClinicianButton", isInvisible());
    }

    @Test
    public void testStaffListHidden() {
        verifyThat("#staffListButton", isInvisible());
    }

    // Test staff-only buttons are hidden

    @Test
    public void testClinicianDetailsHidden() {
        verifyThat("#viewClinicianButton", isInvisible());
    }

    @Test
    public void testClientSearchHidden() {
        verifyThat("#searchButton", isInvisible());
    }

    @Test
    public void testTranplantRequestsHidden() {
        verifyThat("#transplantsButton", isInvisible());
    }
}