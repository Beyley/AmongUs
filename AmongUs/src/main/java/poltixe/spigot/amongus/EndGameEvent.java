package poltixe.spigot.amongus;

import org.bukkit.event.*;

public class EndGameEvent extends Event {
    public EndGameEvent() {
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
