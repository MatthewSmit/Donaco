package com.humanharvest.organz.commands.modify;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.JSONConverter;
import com.humanharvest.organz.utilities.serialization.JSONFileWriter;
import picocli.CommandLine.Command;

/**
 * Command line to save the current information of the Clients onto a JSON file using the GSON API.
 */
@Command(name = "save", description = "Save clients to file", sortOptions = false)
public class Save implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Save.class.getName());
    private static final File FILE = new File("savefile.json");

    private ClientManager manager;

    public Save() {
        manager = State.getClientManager();
    }

    public Save(ClientManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        List<Client> clients = manager.getClients();
        if (clients.isEmpty()) {
            System.out.println("No clients exist, nothing to save");
        } else {
            try (JSONFileWriter<Client> clientWriter = new JSONFileWriter<>(FILE, Client.class)) {
                clientWriter.overwriteWith(clients);
                LOGGER.log(Level.INFO, String.format("Saved %s clients to file", clients.size()));
                HistoryItem save = new HistoryItem("SAVE",
                        String.format("The system's current state was saved to '%s'.", FILE.getName()));
                JSONConverter.updateHistory(save, "action_history.json");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Could not save to file: " + FILE.getName(), e);
            }
        }
    }
}
