package seng302.Commands;

import picocli.CommandLine.Command;
import seng302.Actions.ActionInvoker;
import seng302.App;

@Command(name = "redo", description = "Redo an undone change.")
public class Redo implements Runnable {

    private ActionInvoker invoker;


    public Redo() {
        invoker = App.getInvoker();
    }

    public Redo(ActionInvoker invoker) {
        this.invoker = invoker;
    }

    public void run() {
        invoker.redo();
    }
}
