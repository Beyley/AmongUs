package poltixe.spigot.amongus;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.metadata.*;
import org.bukkit.scoreboard.*;

import net.md_5.bungee.api.ChatColor;

import poltixe.spigot.minigamequeue.*;

public class EventListener implements Listener {
    private App app = App.getPlugin(App.class);

    FileConfiguration config = App.getPlugin(App.class).getConfig();
    static Random r = new Random();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player target = event.getPlayer();

        // Checks if the game has already started
        if (app.gameStarted) {
            // Set joining player to spectator
            target.setGameMode(GameMode.SPECTATOR);
        } else {
            // Give player base metadata to not crash on checking in later code
            target.setMetadata("imposter", new FixedMetadataValue(App.getPlugin(App.class), false));
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        // Get the player
        Player target = event.getPlayer();

        // Send a PlayerDieEvent to say that the player died with the argument that it
        // was a disconnect kill
        PlayerDieEvent playerDieEvent = new PlayerDieEvent(target, true);
        Bukkit.getPluginManager().callEvent(playerDieEvent);
    }

    @EventHandler
    public void onPlayerDie(PlayerDieEvent event) {

    }

    public Scoreboard createScoreboard(boolean imposter) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("scoreboard", "dummy");

        objective.setDisplayName("Among Us");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score first;
        Score fourth;
        Score fifth = objective.getScore("");

        if (imposter) {
            first = objective.getScore(ChatColor.BOLD + "Role : " + ChatColor.RED + "Imposter");
        } else {
            first = objective.getScore(ChatColor.BOLD + "Role : " + ChatColor.BLUE + "Crewmate");
        }
        Score second = objective.getScore("");
        Score third = objective.getScore("Imposters: ");
        if (imposter) {
            fourth = objective.getScore(ChatColor.RED + app.imposter1.player.getName());
            if (app.imposter2 != null)
                fifth = objective.getScore(ChatColor.RED + app.imposter2.player.getName());
        } else {
            fourth = objective.getScore(ChatColor.RED + "???");
            if (app.imposter2 != null)
                fifth = objective.getScore(ChatColor.RED + "???");
        }

        first.setScore(4);
        second.setScore(3);
        third.setScore(2);
        fourth.setScore(1);
        fifth.setScore(0);

        return scoreboard;
    }

    private static Object[] getRandomPlayerNoCopies(PlayerState[] array, int lastSelection) {
        array = Arrays.stream(array).filter(Objects::nonNull).toArray(PlayerState[]::new);

        Object[] finalArray = new Object[2];

        int rnd = r.nextInt(array.length);

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

        finalArray[0] = array[rnd];
        finalArray[1] = rnd;

        return finalArray;
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        app.gameStarted = true;

        Object[] players = Bukkit.getOnlinePlayers().toArray();

        for (int i = 0; i < players.length; i++) {
            Player target = (Player) players[i];

            app.playerStates[i] = new PlayerState(target, false, true);
        }

        if (players.length < 7) {
            Object[] tempObject = getRandomPlayerNoCopies(app.playerStates, -1);

            app.playerStates[(int) tempObject[1]].imposter = true;
            app.imposter1 = app.playerStates[(int) tempObject[1]];
        } else {
            Object[] tempObject1 = getRandomPlayerNoCopies(app.playerStates, -1);
            Object[] tempObject2 = getRandomPlayerNoCopies(app.playerStates, (int) tempObject1[1]);

            app.playerStates[(int) tempObject1[1]].imposter = true;
            app.imposter1 = app.playerStates[(int) tempObject1[1]];

            app.playerStates[(int) tempObject2[1]].imposter = true;
            app.imposter2 = app.playerStates[(int) tempObject2[1]];
        }

        for (PlayerState state : app.playerStates) {
            state.player.setGameMode(GameMode.SURVIVAL);

            if (state.imposter) {
                state.player.sendTitle(ChatColor.BOLD + "You are an " + ChatColor.RED + "Imposter", null, 10, 70, 20);
                state.player.setScoreboard(createScoreboard(true));
            } else {
                state.player.sendTitle(ChatColor.BOLD + "You are a " + ChatColor.BLUE + "Crewmate", null, 10, 70, 20);
                state.player.setScoreboard(createScoreboard(false));
            }
        }
    }
}
