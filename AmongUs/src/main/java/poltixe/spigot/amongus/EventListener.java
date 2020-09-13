package poltixe.spigot.amongus;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.bukkit.Bukkit;
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
    // App app = new App();

    FileConfiguration config = App.getPlugin(App.class).getConfig();
    static Random r = new Random();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player target = event.getPlayer();

        target.setMetadata("imposter", new FixedMetadataValue(App.getPlugin(App.class), false));
    }

    public Scoreboard createScoreboard() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("scoreboard", "dummy");

        objective.setDisplayName("Custom Scoreboard");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score first = objective.getScore("First Line");
        Score second = objective.getScore("Second Line");
        Score third = objective.getScore("Third Line");

        first.setScore(2);
        second.setScore(1);
        third.setScore(0);

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
        App app = App.getPlugin(App.class); // .gameStarted = true;

        app.gameStarted = true;

        Object[] players = Bukkit.getOnlinePlayers().toArray();

        for (int i = 0; i < players.length; i++) {
            Player target = (Player) players[i];
            // Bukkit.broadcastMessage(String.valueOf(i));
            // Bukkit.broadcastMessage(target.getName());

            // Bukkit.broadcastMessage(app.toString());

            app.playerStates[i] = new PlayerState(target, false, true);
        }

        if (players.length < 7) {
            Object[] tempObject = getRandomPlayerNoCopies(app.playerStates, -1);

            // Bukkit.broadcastMessage(String.valueOf((int) tempObject[1]));
            // Bukkit.broadcastMessage(((PlayerState) tempObject[0]).player.getName());

            // Bukkit.broadcastMessage(app.toString());

            app.playerStates[(int) tempObject[1]].imposter = true;
            // app.playerStates[(int) tempObject[1]].player.sendMessage(ChatColor.BOLD +
            // "You are an imposter");
            // app.playerStates[(int) tempObject[1]].player.sendTitle(ChatColor.BOLD + "You
            // are an imposter", null, 10, 70,
            // 20);
        } else {
            Object[] tempObject1 = getRandomPlayerNoCopies(app.playerStates, -1);
            Object[] tempObject2 = getRandomPlayerNoCopies(app.playerStates, (int) tempObject1[1]);

            app.playerStates[(int) tempObject1[1]].imposter = true;
            // app.playerStates[(int) tempObject1[1]].player.sendTitle(ChatColor.BOLD + "You
            // are an imposter", null, 10,
            // 70, 20);
            app.playerStates[(int) tempObject2[1]].imposter = true;
            // app.playerStates[(int) tempObject2[1]].player.sendTitle(ChatColor.BOLD + "You
            // are an imposter", null, 10,
            // 70, 20);
        }

        for (PlayerState state : app.playerStates) {
            if (state.imposter) {
                state.player.sendTitle(ChatColor.BOLD + "You are an " + ChatColor.RED + "Imposter", null, 10, 70, 20);
                state.player.setScoreboard(createScoreboard());
            } else {
                state.player.sendTitle(ChatColor.BOLD + "You are a " + ChatColor.BLUE + "Crewmate", null, 10, 70, 20);
                state.player.setScoreboard(createScoreboard());
            }
        }
    }
}
