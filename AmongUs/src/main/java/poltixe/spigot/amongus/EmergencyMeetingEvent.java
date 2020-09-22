package poltixe.spigot.amongus;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EmergencyMeetingEvent extends Event {
    Player caller;

    public EmergencyMeetingEvent(Player caller) {
        this.caller = caller;
    }

    public Player getMeetingCaller() {
        return this.caller;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
