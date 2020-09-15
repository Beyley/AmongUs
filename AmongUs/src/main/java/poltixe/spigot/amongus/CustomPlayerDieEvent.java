package poltixe.spigot.amongus;

import org.bukkit.entity.Player;
import org.bukkit.event.*;

//Event called when a player needs to be registered as dead by the plugin
public class CustomPlayerDieEvent extends Event {
    // TODO perhaps create a DeathState object?
    
    // The player in question
    private final Player player;
    // Bool to store whether or not the kill was because of a disconnect
    private final Boolean disconnectKill;

    // Defines the constructor for the event taking the player and a bool of whether
    // or not it was a disconnect kill
    public CustomPlayerDieEvent(Player player, boolean disconnectKill) {
        this.player = player;
        this.disconnectKill = disconnectKill;
    }

    // Gets the player object
    public Player getPlayer() {
        return this.player;
    }

    // Gets whether or not it was a disconnect kill
    public Boolean ifDisconnectKill() {
        return this.disconnectKill;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
