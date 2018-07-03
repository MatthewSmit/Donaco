package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.ModifyClientObject;
import com.humanharvest.organz.state.ClientManager;
import org.springframework.beans.BeanUtils;

public class ModifyClientByObjectAction extends Action {

    private Client client;
    private ClientManager manager;
    private ModifyClientObject oldClientDetails;
    private ModifyClientObject newClientDetails;

    /**
     * Create a new Action
     * @param client The client to be modified
     * @param manager // TODO
     */
    public ModifyClientByObjectAction(Client client, ClientManager manager, ModifyClientObject oldClientDetails,
            ModifyClientObject newClientDetails) {
        this.client = client;
        this.manager = manager;
        this.oldClientDetails = oldClientDetails;
        this.newClientDetails = newClientDetails;
    }

    @Override
    protected void execute() {
        BeanUtils.copyProperties(newClientDetails, client, newClientDetails.getUnmodifiedFields());
        manager.applyChangesTo(client);
    }

    @Override
    protected void unExecute() {
        BeanUtils.copyProperties(oldClientDetails, client, oldClientDetails.getUnmodifiedFields());
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText() {
        return "Todo";
    }

    @Override
    public String getUnexecuteText() {
        return "Todo";
    }
}
