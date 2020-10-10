package poltixe.spigot.amongus;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.*;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
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

        target.setGameMode(GameMode.SPECTATOR);

        target.setMetadata("frozen", new FixedMetadataValue(App.getPlugin(App.class), false));

        // Checks if the game has already started
        if (!app.gameState.gameStarted) {
            // If not, give player base metadata to not crash on checking in later code
            target.setMetadata("imposter", new FixedMetadataValue(App.getPlugin(App.class), false));
        }
    }

    @EventHandler
    public void onPlayerVoteEvent(PlayerVoteEvent event) {
        event.getVoter().playerTheyVotedFor = event.getPlayerVotedFor();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().getMetadata("frozen").get(0).asBoolean()) {
            event.setCancelled(true);
        }

        if (!app.gameState.gameStarted) {// Restarts the day progression, kinda bodgey
            Bukkit.getWorld("world").setTime(8000);
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

    public static PlayerState[] stripNullFromPlayerStates(PlayerState[] array) {
        return Arrays.stream(array).filter(Objects::nonNull).toArray(PlayerState[]::new);
    }

    // Called when the game needs to update information on alive players
    @EventHandler
    public void onCustomPlayerDie(CustomPlayerDieEvent event) {
        if (!app.gameState.gameStarted)
            return;

        // Gets the target (the player who died)
        Player target = event.getPlayer();

        PlayerState state = PlayerState.getPlayerStateFromName(target.getName());

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
            }, 400, 40000000);
        }

    }

    // Gets a random player without the same one being chosen
    private static Object[] getRandomPlayerNoCopies(PlayerState[] array, int lastSelection) {
        // Remove all null objects from array
        array = stripNullFromPlayerStates(array);

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
        app.gameState.gameStarted = true;

        // Get all players
        Object[] players = Bukkit.getOnlinePlayers().toArray();

        // Loop through all players
        for (int i = 0; i < players.length; i++) {
            // Gets the target
            Player target = (Player) players[i];

            // Defined their playerstate in the global playerStates array
            app.playerStates[i] = new PlayerState(target, false, true, app.config.getInt("amountOfMeetings"));
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
            app.gameState.imposters[0] = app.playerStates[(int) tempObject[1]];
        } else {
            // Create 2 tempObjects that will store the output of the random PlayerState
            // pickers
            // TODO maybe move into own object to avoid spaghetti
            Object[] tempObject1 = getRandomPlayerNoCopies(app.playerStates, -1);
            Object[] tempObject2 = getRandomPlayerNoCopies(app.playerStates, (int) tempObject1[1]);

            // Sets the imposter flag for the player to true
            app.playerStates[(int) tempObject1[1]].imposter = true;
            // Sets the global imposter1 variable to the PlayerState of the imposter
            app.gameState.imposters[0] = app.playerStates[(int) tempObject1[1]];

            // Sets the imposter flag for the player to true
            app.playerStates[(int) tempObject2[1]].imposter = true;
            // Sets the global imposter2 variable to the PlayerState of the imposter
            app.gameState.imposters[2] = app.playerStates[(int) tempObject2[1]];
        }

        PlayerState[] nullStrippedArray = stripNullFromPlayerStates(app.playerStates);

        // Loop through all PlayerStates
        for (PlayerState state : nullStrippedArray) {
            // Bukkit.broadcastMessage(state.player.getName());

            // Bukkit.broadcastMessage(state.toString());

            // Bukkit.broadcastMessage(String.valueOf(state.alive));

            // Set player to survival
            state.player.setGameMode(GameMode.SURVIVAL);

            // Checks if the player is an imposter
            if (state.imposter) {
                // Send the title that the player is an imposter
                state.player.sendTitle(ChatColor.BOLD + "You are an " + ChatColor.RED + "Imposter", null, 10, 70, 20);
                // Sets the players scoreboard to the imposter variant
                state.player.setScoreboard(ScoreboardHandler.updateScoreboard(state));

                // Give the imposter the items
                state.player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));

                ItemStack potion = new Potion(PotionType.INVISIBILITY).toItemStack(2);
                ItemMeta itemMeta = potion.getItemMeta();
                itemMeta.setDisplayName("Vents");

                state.player.getInventory().addItem(potion);
            } else {
                // Send the title that the player is an crewmate
                state.player.sendTitle(ChatColor.BOLD + "You are a " + ChatColor.BLUE + "Crewmate", null, 10, 70, 20);
                // Sets the players scoreboard to the crewmate variant
                state.player.setScoreboard(ScoreboardHandler.updateScoreboard(state));
            }

            app.getServer().getScheduler().scheduleSyncRepeatingTask(app, new Runnable() {
                public void run() {
                    for (PlayerState updateScoreboardState : stripNullFromPlayerStates(app.playerStates))
                        updateScoreboardState.player
                                .setScoreboard(ScoreboardHandler.updateScoreboard(updateScoreboardState));

                    int amountOfImposters = 0;
                    int amountOfCrewmates = 0;

                    for (PlayerState checkIfEndGameState : stripNullFromPlayerStates(app.playerStates)) {
                        if (checkIfEndGameState.alive) {
                            if (checkIfEndGameState.imposter) {
                                amountOfImposters += 1;
                            } else {
                                amountOfCrewmates += 1;
                            }
                        }
                    }

                    if (amountOfImposters >= amountOfCrewmates) {
                        EndGameEvent endGameEvent = new EndGameEvent();

                        Bukkit.getPluginManager().callEvent(endGameEvent);
                    }
                }
            }, 0, 20);
        }
    }

    public void onEndGameEvent(EndGameEvent event) {
        int amountOfImposters = 0;
        int amountOfCrewmates = 0;

        for (PlayerState state : stripNullFromPlayerStates(app.playerStates)) {
            state.player.setGameMode(GameMode.SPECTATOR);

            if (state.alive) {
                if (state.imposter) {
                    amountOfImposters += 1;
                } else {
                    amountOfCrewmates += 1;
                }
            }
        }

        if (amountOfImposters >= amountOfCrewmates) {
            Bukkit.broadcastMessage("The Imposters have won!");
        } else {
            Bukkit.broadcastMessage("The Crewmates have won!");
        }

        app.gameState.gameEnded = true;
    }
}
