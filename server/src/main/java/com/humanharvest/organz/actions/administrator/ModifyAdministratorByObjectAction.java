package com.humanharvest.organz.actions.administrator;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.state.AdministratorManager;
import com.humanharvest.organz.views.administrator.ModifyAdministratorObject;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

public class ModifyAdministratorByObjectAction extends Action {

    private final Administrator administrator;
    private final AdministratorManager manager;
    private final ModifyAdministratorObject oldDetails;
    private final ModifyAdministratorObject newDetails;

    public ModifyAdministratorByObjectAction(Administrator administrator,
            AdministratorManager manager,
            ModifyAdministratorObject oldDetails,
            ModifyAdministratorObject newDetails) {
        this.administrator = administrator;
        this.manager = manager;
        this.oldDetails = oldDetails;
        this.newDetails = newDetails;
    }

    @Override
    protected void execute() {
        BeanUtils.copyProperties(newDetails, administrator, newDetails.getUnmodifiedFields());
        manager.applyChangesTo(administrator);
    }

    @Override
    protected void unExecute() {
        BeanUtils.copyProperties(oldDetails, administrator, oldDetails.getUnmodifiedFields());
        manager.applyChangesTo(administrator);
    }


    @Override
    public String getExecuteText() {
        String changesText = newDetails.getModifiedFields().stream()
                .map(Field::getName)
                .map(this::unCamelCase)
                .collect(Collectors.joining("\n"));

        return String.format("Updated details for admin %s. \n"
                        + "These changes were made: \n\n%s",
                administrator.getUsername(), changesText);
    }

    @Override
    public String getUnexecuteText() {
        String changesText = oldDetails.getModifiedFields().stream()
                .map(Field::getName)
                .map(this::unCamelCase)
                .collect(Collectors.joining("\n"));

        return String.format("Reversed update for admin %s. \n"
                        + "These changes were reversed: \n\n%s",
                administrator.getUsername(), changesText);
    }

    private String unCamelCase(String inCamelCase) {
        String unCamelCased = inCamelCase.replaceAll("([a-z])([A-Z]+)", "$1 $2");
        return unCamelCased.substring(0, 1).toUpperCase() + unCamelCased.substring(1);
    }
    @Override
    public Object getModifiedObject() {
        return administrator;
    }
}
