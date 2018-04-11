package seng302.Actions.Clinician;

import seng302.Actions.Action;
import seng302.Clinician;
import seng302.Donor;
import seng302.State.ClinicianManager;
import seng302.State.DonorManager;

/**
 * A reversible clinician creation action
 */
public class CreateClinicianAction implements Action {

    private Clinician clinician;
    private ClinicianManager manager;

    /**
     * Create a new Action
     * @param clinician The Clinician to be created
     * @param manager The ClinicianManager to apply changes to
     */
    public CreateClinicianAction(Clinician clinician, ClinicianManager manager) {
        this.clinician = clinician;
        this.manager = manager;
    }

    /**
     * Simply add the clinician to the DonorManager
     */
    @Override
    public void execute() {
        manager.addClinician(clinician);
    }

    /**
     * Simply remove the clinician from the DonorManager
     */
    @Override
    public void unExecute() {
        manager.removeClinician(clinician);
    }

    @Override
    public String getExecuteText() {
        return String.format("Created clinician %s", clinician.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Removed clinician %s", clinician.getFullName());
    }
}
