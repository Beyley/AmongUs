package poltixe.spigot.amongus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;

public class ScoreboardHandler {
    // Get an instance of the plugin
    private static App app = App.getPlugin(App.class);

    // Used to create the custom Among Us scoreboard
    public static Scoreboard updateScoreboard(PlayerState state) {
        // Creates a new scoreboard
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        // Creates a new objective with a dummy critera
        Objective objective = scoreboard.registerNewObjective("scoreboard", "dummy");

        // Sets the name of the scoreboard
        objective.setDisplayName("Among Us");
        // Sets the displayslot
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Define the lines that the scoreboard will have
        Score first = objective.getScore(ChatColor.BOLD + "Role : " + ChatColor.RED + "????");
        Score second = objective.getScore("");
        Score third = objective.getScore("Imposters: ");
        Score fourth = objective.getScore("");
        Score fifth = objective.getScore("");
        Score sixth = objective.getScore("Amount of meetings left : ????");

        // Checks if the player is an imposter
        if (state.imposter) {
            // Display the imposter name as their role
            first = objective.getScore(ChatColor.BOLD + "Role : " + ChatColor.RED + "Imposter");

            // Displays the imposter names
            if (app.gameState.imposters[0].alive) {
                fourth = objective.getScore(ChatColor.RED + app.gameState.imposters[0].player.getName());
            } else {
                fourth = objective.getScore(ChatColor.RED + "DEAD:" + app.gameState.imposters[0].player.getName());
            }
            if (app.gameState.imposters[1] != null) {
                if (app.gameState.imposters[1].alive) {
                    fifth = objective.getScore(ChatColor.RED + app.gameState.imposters[1].player.getName());
                } else {
                    fifth = objective.getScore(ChatColor.RED + "DEAD:" + app.gameState.imposters[1].player.getName());
                }
            }
        } else {
            // Display the crewmate name as their role
            first = objective.getScore(ChatColor.BOLD + "Role : " + ChatColor.BLUE + "Crewmate");

            // Displays the names as question marks as they are not known yet
            if (app.gameState.imposters[0].alive) {
                fourth = objective.getScore(ChatColor.RED + "???");
            } else {
                fourth = objective.getScore(ChatColor.RED + "DEAD:" + app.gameState.imposters[0].player.getName());
            }

            if (app.gameState.imposters[1] != null) {
                if (app.gameState.imposters[1].alive) {
                    fifth = objective.getScore(ChatColor.RED + "???");
                } else {
                    fifth = objective.getScore(ChatColor.RED + "DEAD:" + app.gameState.imposters[1].player.getName());
                }
            }
        }

        if (app.gameState.gameStarted) {
            sixth = objective.getScore("Amount of meetings left : " + state.meetingsLeft);
        }

        // Set the lines scores
        first.setScore(5);
        second.setScore(4);
        third.setScore(3);
        fourth.setScore(2);
        fifth.setScore(1);
        sixth.setScore(0);

        return scoreboard;
    }
}
