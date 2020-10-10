package poltixe.spigot.amongus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class CommandVote implements CommandExecutor {
    App app = App.getPlugin(App.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerState senderState = PlayerState.getPlayerStateFromName(sender.getName());

        if (!senderState.alive) {
            senderState.player.sendMessage(ChatColor.RED + "You are dead!");
            return true;
        }

        if (app.gameState.isVotingTime) {
            PlayerState personToVoteFor = PlayerState.getPlayerStateFromName(args[0].toString());

            if (personToVoteFor == null) {
                sender.sendMessage(ChatColor.RED + "That player does not exist!");
                return true;
            }

            if (!personToVoteFor.alive) {
                sender.sendMessage(ChatColor.RED + "That player is dead!");
                return true;
            }

            if (personToVoteFor == senderState) {
                sender.sendMessage(ChatColor.RED + "uh ok then..");
                return true;
            }

            sender.sendMessage(ChatColor.BLUE + "Voting for " + personToVoteFor.player.getName());

            PlayerVoteEvent playerVoteEvent = new PlayerVoteEvent(senderState, personToVoteFor);

            Bukkit.getPluginManager().callEvent(playerVoteEvent);
        } else {
            sender.sendMessage(ChatColor.RED + "It is not voting time!");
            return true;
        }
        return true;
    }

}
