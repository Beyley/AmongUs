package poltixe.spigot.amongus;

import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class PlayerDieEvent extends Event {
    private final Player player;
    private final Boolean disconnectKill;

    public PlayerDieEvent(Player player, boolean disconnectKill) {
        this.player = player;
        this.disconnectKill = disconnectKill;
    }

    public Player getPlayer() {
        return this.player;
    }

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
