package poltixe.spigot.amongus;

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
        System.out.println("in command code " + sender.getName());
        PlayerState senderState = PlayerState.getPlayerState(sender.getName());

        if (senderState.meetingsLeft == 0) {
            senderState.player.sendMessage(ChatColor.RED + "You have no emergency meetings left!");
            return true;
        }

        if (app.gameState.inMeeting) {
            senderState.player.sendMessage(ChatColor.RED + "You are already in a meeting!");
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
                    if (!state.alive) {
                        state.player.sendTitle("Emergency Meeting Start!", null, 10, 70, 20);
                        state.player.setMetadata("frozen", new FixedMetadataValue(App.getPlugin(App.class), true));
                        state.player.getPlayer().setGameMode(GameMode.SPECTATOR);
                    }
                }

                app.getServer().getScheduler().scheduleSyncRepeatingTask(app, new Runnable() {
                    public void run() {
                        for (PlayerState state : EventListener.stripNullFromPlayerStates(app.playerStates)) {
                            if (!state.alive)
                                state.player.sendTitle("Discussion time over!", null, 10, 70, 20);
                        }

                        app.gameState.isVotingTime = true;

                        app.getServer().getScheduler().scheduleSyncRepeatingTask(app, new Runnable() {
                            public void run() {
                                for (PlayerState state : EventListener.stripNullFromPlayerStates(app.playerStates)) {
                                    if (!state.alive) {
                                        state.player.sendTitle("Voting time over!", null, 10, 70, 20);
                                        state.player.setMetadata("frozen",
                                                new FixedMetadataValue(App.getPlugin(App.class), false));
                                        state.player.getPlayer().setGameMode(GameMode.SURVIVAL);
                                    }
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
