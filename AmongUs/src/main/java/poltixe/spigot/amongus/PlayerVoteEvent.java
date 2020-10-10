package poltixe.spigot.amongus;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerVoteEvent extends Event {
    private PlayerState voter;
    private PlayerState playerVotedFor;

    public PlayerVoteEvent(PlayerState voter, PlayerState playerVotedFor) {
        this.voter = voter;
        this.playerVotedFor = playerVotedFor;
    }

    public PlayerState getVoter() {
        return this.voter;
    }

    public PlayerState getPlayerVotedFor() {
        return this.playerVotedFor;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
