package seng302.Controller.Administrator;

import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;

import seng302.Administrator;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext.WindowContextBuilder;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class SidebarControllerAdministratorTest extends ControllerTest {

    private Administrator testAdmin = new Administrator("username", "password");


    @Override
    protected Page getPage() {
        return Page.SIDEBAR;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(testAdmin);
        State.getAdministratorManager().addAdministrator(testAdmin);
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

    @Test
    public void testClickOnLogout() {
        clickOn("#logoutButton");
        assertEquals(Page.LANDING, mainController.getCurrentPage());
    }

    // Test clicking on buttons to go to another screen

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
    public void testClickOnCreateAdministrator() {
        clickOn("#createAdminButton");
        assertEquals(Page.CREATE_ADMINISTRATOR, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnCreateClinician() {
        clickOn("#createClinicianButton");
        assertEquals(Page.CREATE_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnHistory() {
        clickOn("Action history");
        assertEquals(Page.HISTORY, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnClinicianDetails() {
        clickOn("#viewClinicianButton");
        assertEquals(Page.VIEW_CLINICIAN, mainController.getCurrentPage());
    }

    // Test client-only buttons are hidden

    @Test
    public void testClientDetailsHidden() {
        verifyThat("#viewClientButton", isInvisible());
    }

    @Test
    public void testDonateOrgansHidden() {
        verifyThat("#registerOrganDonationButton", isInvisible());
    }

    @Test
    public void testRequestOrgansHidden() {
        verifyThat("#requestOrganDonationButton", isInvisible());
    }

    @Test
    public void testMedicationsHidden() {
        verifyThat("#viewMedicationsButton", isInvisible());
    }

    @Test
    public void testMedicalHistoryHidden() {
        verifyThat("#illnessHistoryButton", isInvisible());
    }


}
