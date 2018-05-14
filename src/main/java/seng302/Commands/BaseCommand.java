package seng302.Commands;

import seng302.Commands.Modify.CreateClient;
import seng302.Commands.Modify.CreateClinician;
import seng302.Commands.Modify.DeleteClient;
import seng302.Commands.Modify.Load;
import seng302.Commands.Modify.Redo;
import seng302.Commands.Modify.Save;
import seng302.Commands.Modify.SetAttribute;
import seng302.Commands.Modify.SetOrganStatus;
import seng302.Commands.Modify.Undo;
import seng302.Commands.View.GetChanges;
import seng302.Commands.View.PrintAllInfo;
import seng302.Commands.View.PrintAllOrgan;
import seng302.Commands.View.PrintClientInfo;
import seng302.Commands.View.PrintClientOrgan;

import picocli.CommandLine.Command;

/**
 * The main command hub used to access the other commands within the program such as save, help, createuser etc.
 */
@Command(name = "OrgaNZ",
        description = "OrgaNZ is a command based management tool for the human harvest client registration system.",
        subcommands = {
                CreateClient.class,
                CreateClinician.class,
                SetAttribute.class,
                SetOrganStatus.class,
                DeleteClient.class,
                PrintAllInfo.class,
                PrintAllOrgan.class,
                PrintClientInfo.class,
                PrintClientOrgan.class,
                GetChanges.class,
                Save.class,
                Load.class,
                Help.class,
                Undo.class,
                Redo.class
        })

public class BaseCommand implements Runnable {
    public void run() {
        System.out.println("Invalid command");
    }
}
