package poltixe.spigot.amongus;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.*;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.*;

import net.md_5.bungee.api.ChatColor;

import poltixe.spigot.minigamequeue.*;

//The event listener
public class EventListener implements Listener {
    // Get an instance of the plugin
    private App app = App.getPlugin(App.class);

    // Gets the configuration file
    FileConfiguration config = app.getConfig();
    // Creates a new random object
    static Random r = new Random();

    // Called when a player joins the server
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player target = event.getPlayer();

        // Checks if the game has already started
        if (!app.gameStarted) {
            // If not, give player base metadata to not crash on checking in later code
            target.setMetadata("imposter", new FixedMetadataValue(App.getPlugin(App.class), false));
        }
    }

    // Called when a player disconnects from the server
    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        // Disable leave message
        event.setQuitMessage("");

        // Get the player
        Player target = event.getPlayer();

        // Send a PlayerDieEvent to say that the player died with the argument that it
        // was a disconnect kill
        CustomPlayerDieEvent playerDieEvent = new CustomPlayerDieEvent(target, true);
        Bukkit.getPluginManager().callEvent(playerDieEvent);
    }

    // Called when a player dies
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Disable death message
        event.setDeathMessage("");

        // Get the player
        Player target = event.getEntity();

        // Send a PlayerDieEvent to say that the player died with the argument that it
        // was not a disconnect kill
        CustomPlayerDieEvent playerDieEvent = new CustomPlayerDieEvent(target, false);
        Bukkit.getPluginManager().callEvent(playerDieEvent);
    }

    // Called when the game needs to update information on alive players
    @EventHandler
    public void onCustomPlayerDie(CustomPlayerDieEvent event) {
        // Gets the target (the player who died)
        Player target = event.getPlayer();

        // Loop through all player states
        for (PlayerState state : app.playerStates) {
            // Checks if the state is the same as the player that died
            if (state.player == target) {
                // Sets the player to be dead
                state.alive = false;

                // Checks if the player died due to disconnection or not
                if (event.ifDisconnectKill()) {
                    // Broadcasts that the player disconnected
                    Bukkit.broadcastMessage(ChatColor.RED + target.getName() + " has disconnected!");
                } else {
                    // Sets the players gamemoode to spectator
                    target.setGameMode(GameMode.SPECTATOR);

                    // Gets their location
                    Location deathLocation = target.getLocation();

                    // Sets a timer for 20 seconds since death
                    app.getServer().getScheduler().scheduleSyncRepeatingTask(app, new Runnable() {
                        public void run() {
                            // Strikes lightning on the players death location
                            target.getWorld().strikeLightning(deathLocation);
                        }
                    }, 0, 400);
                }
            }
        }
    }

    // Used to create the custom Among Us scoreboard
    // TODO move to separate file in case anything else needs this
    public Scoreboard createScoreboard(boolean imposter) {
        // Creates a new scoreboard
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        // Creates a new objective with a dummy critera
        Objective objective = scoreboard.registerNewObjective("scoreboard", "dummy");

        // Sets the name of the scoreboard
        objective.setDisplayName("Among Us");
        // Sets the displayslot
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Define the lines that the scoreboard will have
        Score first;
        Score second = objective.getScore("");
        Score third = objective.getScore("Imposters: ");
        Score fourth;
        Score fifth = objective.getScore("");

        // Checks if the player is an imposter
        if (imposter) {
            // Display the imposter name as their role
            first = objective.getScore(ChatColor.BOLD + "Role : " + ChatColor.RED + "Imposter");

            // Displays the imposter names
            fourth = objective.getScore(ChatColor.RED + app.imposter1.player.getName());
            if (app.imposter2 != null)
                fifth = objective.getScore(ChatColor.RED + app.imposter2.player.getName());
        } else {
            // Display the crewmate name as their role
            first = objective.getScore(ChatColor.BOLD + "Role : " + ChatColor.BLUE + "Crewmate");

            // Displays the names as question marks as they are not known yet
            fourth = objective.getScore(ChatColor.RED + "???");
            if (app.imposter2 != null)
                fifth = objective.getScore(ChatColor.RED + "???");
        }

        // Set the lines scores
        first.setScore(4);
        second.setScore(3);
        third.setScore(2);
        fourth.setScore(1);
        fifth.setScore(0);

        return scoreboard;
    }

    // Gets a random player without the same one being chosen
    private static Object[] getRandomPlayerNoCopies(PlayerState[] array, int lastSelection) {
        // Remove all null objects from array
        // TODO split into separate function (maybe even file)
        array = Arrays.stream(array).filter(Objects::nonNull).toArray(PlayerState[]::new);

        // Creates a blank array of size 2
        // TODO create an object instead of this messyness
        Object[] finalArray = new Object[2];

        // Get a new random numberr
        int rnd = r.nextInt(array.length);

        // Make sure its not a copy
        // TODO THIS IS TERRIBLE PLEASE CHANGE THIS
        if (rnd == lastSelection)
            rnd = r.nextInt(array.length);
        if (rnd == lastSelection)
            rnd = r.nextInt(array.length);
        if (rnd == lastSelection)
            rnd = r.nextInt(array.length);
        if (rnd == lastSelection)
            rnd = r.nextInt(array.length);
        if (rnd == lastSelection)
            rnd = r.nextInt(array.length);
        if (rnd == lastSelection)
            rnd = r.nextInt(array.length);
        if (rnd == lastSelection)
            rnd = r.nextInt(array.length);

        // Set the first object to the playerstate that is chosen
        finalArray[0] = array[rnd];
        // Set the second object to the index of the playerstate chosen
        // TODO kinda odd to do, maybe look into a better way
        finalArray[1] = rnd;

        return finalArray;
    }

    // Called when the game starts
    @EventHandler
    public void onGameStart(GameStartEvent event) {
        // Sets the global gameStarted variable to true
        app.gameStarted = true;

        // Get all players
        Object[] players = Bukkit.getOnlinePlayers().toArray();

        // Loop through all players
        for (int i = 0; i < players.length; i++) {
            // Gets the target
            Player target = (Player) players[i];

            // Defined their playerstate in the global playerStates array
            app.playerStates[i] = new PlayerState(target, false, true);
        }

        // Checks if the players are less then 7
        if (players.length < 7) {
            // Create a tempObject that will store the output of the random PlayerState
            // picker
            // TODO maybe move into own object to avoid spaghetti
            Object[] tempObject = getRandomPlayerNoCopies(app.playerStates, -1);

            // Sets the imposter flag for the player to true
            app.playerStates[(int) tempObject[1]].imposter = true;
            // Sets the global imposter1 variable to the PlayerState of the imposter
            app.imposter1 = app.playerStates[(int) tempObject[1]];
        } else {
            // Create 2 tempObjects that will store the output of the random PlayerState
            // pickers
            // TODO maybe move into own object to avoid spaghetti
            Object[] tempObject1 = getRandomPlayerNoCopies(app.playerStates, -1);
            Object[] tempObject2 = getRandomPlayerNoCopies(app.playerStates, (int) tempObject1[1]);

            // Sets the imposter flag for the player to true
            app.playerStates[(int) tempObject1[1]].imposter = true;
            // Sets the global imposter1 variable to the PlayerState of the imposter
            app.imposter1 = app.playerStates[(int) tempObject1[1]];

            // Sets the imposter flag for the player to true
            app.playerStates[(int) tempObject2[1]].imposter = true;
            // Sets the global imposter2 variable to the PlayerState of the imposter
            app.imposter2 = app.playerStates[(int) tempObject2[1]];
        }

        // Loop through all PlayerStates
        for (PlayerState state : app.playerStates) {
            // Set player to survival
            state.player.setGameMode(GameMode.SURVIVAL);

            // Checks if the player is an imposter
            if (state.imposter) {
                // Send the title that the player is an imposter
                state.player.sendTitle(ChatColor.BOLD + "You are an " + ChatColor.RED + "Imposter", null, 10, 70, 20);
                // Sets the players scoreboard to the imposter variant
                state.player.setScoreboard(createScoreboard(true));

                state.player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                state.player.getInventory().addItem((new Potion(PotionType.INVISIBILITY).toItemStack(2)));
            } else {
                // Send the title that the player is an crewmate
                state.player.sendTitle(ChatColor.BOLD + "You are a " + ChatColor.BLUE + "Crewmate", null, 10, 70, 20);
                // Sets the players scoreboard to the crewmate variant
                state.player.setScoreboard(createScoreboard(false));
            }
        }
    }
}
