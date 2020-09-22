package poltixe.spigot.amongus;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin {
    // Get the config file
    public FileConfiguration config = getConfig();

    // A list of all the player states
    public PlayerState[] playerStates = new PlayerState[10];
    // A bool to store whether or not the game has started
    public GameState gameState = new GameState(null, null);

    // Run when the plugin is enabled
    @Override
    public void onEnable() {
        // Setup default config
        config.addDefault("amountOfMeetings", 1);
        config.options().copyDefaults(true);
        saveConfig();

        // Registers an event listener
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        // Add commands
        this.getCommand("callmeeting").setExecutor(new CommandCallMeeting());
    }
}
