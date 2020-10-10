package poltixe.spigot.amongus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class CommandVote implements CommandExecutor {
    App app = App.getPlugin(App.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (app.gameState.isVotingTime) {
            sender.sendMessage("Voted for the person from the depths from hell");
        } else {
            sender.sendMessage("you arent in a meeting idiot");
        }
        return true;
    }

}
