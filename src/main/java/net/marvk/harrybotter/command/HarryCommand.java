package net.marvk.harrybotter.command;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

public abstract class HarryCommand extends Command {
    protected final String prefix;

    public HarryCommand(final String prefix) {
        this.prefix = prefix;
    }

    protected String getPrefix() {
        return prefix;
    }

    protected String commandPrefix() {
        return prefix + name + " ";
    }

    protected String getActualMessage(final CommandEvent commandEvent) {
        return commandEvent.getMessage().getContentRaw().replaceFirst("^" + commandPrefix(), "");
    }
}
