package poltixe.spigot.amongus;

import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin {
    // public FileConfiguration config = getConfig();

    public PlayerState[] playerStates = new PlayerState[10];
    public boolean gameStarted = false;

    @Override
    public void onEnable() {
        /// config.addDefault("minPlayersForStart", 4);
        /// config.options().copyDefaults(true);
        /// saveConfig();

        getServer().getPluginManager().registerEvents(new EventListener(), this);

        // this.getCommand("readycheck").setExecutor(new CommandCheckReadyStatus());
        // this.getCommand("readyup").setExecutor(new CommandReadyUp());
    }
}
