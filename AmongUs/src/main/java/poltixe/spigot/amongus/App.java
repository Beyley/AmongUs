package poltixe.spigot.amongus;

import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin {
    // Here for when configurable options are a thing
    // public FileConfiguration config = getConfig();

    // A list of all the player states
    public PlayerState[] playerStates = new PlayerState[10];
    // A bool to store whether or not the game has started
    // TODO Create a GameState object for better consistency
    public boolean gameStarted = false;

    // PlayerStates storing both imposters, can probably be implemented in a much
    // better way
    // TODO needs more looking into for better solution
    public PlayerState imposter1 = null;
    public PlayerState imposter2 = null;

    // Run when the plugin is enabled
    @Override
    public void onEnable() {
        // Here for when a config is implemented
        /// config.addDefault("minPlayersForStart", 4);
        /// config.options().copyDefaults(true);
        /// saveConfig();

        // Registers an event listener
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        // Dummy command registers to make implementing commands later easier
        // this.getCommand("readycheck").setExecutor(new CommandCheckReadyStatus());
        // this.getCommand("readyup").setExecutor(new CommandReadyUp());
    }
}
