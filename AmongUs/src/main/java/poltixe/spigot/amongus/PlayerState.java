package poltixe.spigot.amongus;

import org.bukkit.entity.Player;

//State that stores important information about the player
public class PlayerState {
    // Get an instance of the plugin
    private static App app = App.getPlugin(App.class);

    // bool to store whether or not the player is an imposter
    public boolean imposter;
    // bool to store whether or not the player is alive
    public boolean alive;
    // the Player object of the player
    public Player player;
    // How many mettings the player has left
    public int meetingsLeft;

    // the constructor taking the Player object, the imposter value and the alive
    // value
    PlayerState(Player player, boolean imposter, boolean alive, int meetingsLeft) {
        this.imposter = imposter;
        this.alive = alive;
        this.player = player;
        this.meetingsLeft = meetingsLeft;
    }

    public static PlayerState getPlayerState(String playerName) {
        PlayerState toSendState = null;

        PlayerState[] array = EventListener.stripNullFromPlayerStates(app.playerStates);

        for (PlayerState state : array) {
            System.out.println(state.player.getName());
            if (state.player.getName() == playerName) {
                toSendState = state;
            }
        }

        return toSendState;
    }
}
