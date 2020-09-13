package poltixe.spigot.amongus;

import org.bukkit.entity.Player;

public class PlayerState {
    public boolean imposter;
    public boolean alive;
    public Player player;

    PlayerState(Player player, boolean imposter, boolean alive) {
        this.imposter = imposter;
        this.alive = alive;
        this.player = player;
    }
}
