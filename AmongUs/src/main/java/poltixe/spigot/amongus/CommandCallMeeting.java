package poltixe.spigot.amongus;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import net.md_5.bungee.api.ChatColor;

public class CommandCallMeeting implements CommandExecutor {
    App app = App.getPlugin(App.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerState senderState = PlayerState.getPlayerStateFromName(sender.getName());

        if (senderState.meetingsLeft == 0) {
            senderState.player.sendMessage(ChatColor.RED + "You have no emergency meetings left!");
            return true;
        }

        if (app.gameState.inMeeting) {
            senderState.player.sendMessage(ChatColor.RED + "You are already in a meeting!");
            return true;
        }

        if (!senderState.alive) {
            senderState.player.sendMessage(ChatColor.RED + "You are dead!");
            return true;
        }

        senderState.meetingsLeft -= 1;

        senderState.player.setScoreboard(ScoreboardHandler.updateScoreboard(senderState));

        for (PlayerState state : EventListener.stripNullFromPlayerStates(app.playerStates)) {
            state.player.sendTitle("Emergency Meeting in 5 seconds", null, 5, 10, 20);
        }

        app.gameState.inMeeting = true;

        app.getServer().getScheduler().scheduleSyncRepeatingTask(app, new Runnable() {
            public void run() {
                for (PlayerState state : EventListener.stripNullFromPlayerStates(app.playerStates)) {
                    if (state.alive) {
                        state.player.sendTitle("Emergency Meeting Start!", null, 10, 70, 20);
                        state.player.setMetadata("frozen", new FixedMetadataValue(App.getPlugin(App.class), true));
                        state.player.getPlayer().setGameMode(GameMode.SPECTATOR);

                        state.amountOfVotes = 0;
                    }

                    state.playerTheyVotedFor = null;
                }

                app.getServer().getScheduler().scheduleSyncRepeatingTask(app, new Runnable() {
                    public void run() {
                        for (PlayerState state : EventListener.stripNullFromPlayerStates(app.playerStates)) {
                            if (state.alive)
                                state.player.sendTitle("Discussion time over!", null, 10, 70, 20);
                        }

                        app.gameState.isVotingTime = true;

                        app.getServer().getScheduler().scheduleSyncRepeatingTask(app, new Runnable() {
                            public void run() {
                                for (PlayerState state : EventListener.stripNullFromPlayerStates(app.playerStates)) {
                                    if (state.alive) {
                                        state.player.sendTitle("Voting time over!", null, 10, 70, 20);
                                        state.player.setMetadata("frozen",
                                                new FixedMetadataValue(App.getPlugin(App.class), false));
                                        state.player.getPlayer().setGameMode(GameMode.SURVIVAL);

                                        if (state.playerTheyVotedFor != null) {
                                            Bukkit.broadcastMessage(state.player.getName() + " voted for "
                                                    + state.playerTheyVotedFor.player.getName());

                                            state.playerTheyVotedFor.amountOfVotes += 1;
                                        }
                                    }
                                }

                                PlayerState highestVotedFor = new PlayerState(null, false, false, 0);

                                highestVotedFor.amountOfVotes = -1;

                                Boolean tied = false;

                                for (PlayerState state : EventListener.stripNullFromPlayerStates(app.playerStates)) {
                                    if (highestVotedFor.amountOfVotes < state.amountOfVotes) {
                                        highestVotedFor = state;
                                        tied = false;
                                    } else if (highestVotedFor.amountOfVotes == state.amountOfVotes) {
                                        tied = true;
                                    }
                                }

                                if (tied) {
                                    Bukkit.broadcastMessage(ChatColor.RED + "The vote was a tie!");
                                } else {
                                    if (highestVotedFor.imposter) {
                                        Bukkit.broadcastMessage(ChatColor.BLUE + highestVotedFor.player.getName()
                                                + " was an imposter!");
                                    } else {
                                        Bukkit.broadcastMessage(ChatColor.RED + highestVotedFor.player.getName()
                                                + " was not an imposter!");
                                    }

                                    highestVotedFor.player.setHealth(0);
                                }

                                app.gameState.inMeeting = false;
                                app.gameState.isVotingTime = false;
                            }
                        }, app.config.getInt("lengthOfVotingTime") * 20, 40000000);
                    }
                }, app.config.getInt("lengthOfDiscussion") * 20, 40000000);
            }
        }, 100, 40000000);

        return true;
    }
}
