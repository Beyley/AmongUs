package poltixe.spigot.amongus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class CommandCallMeeting implements CommandExecutor {
    App app = App.getPlugin(App.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerState senderState = null;

        for (PlayerState state : app.playerStates) {
            if (state.player.getName() == sender.getName()) {
                senderState = state;
            }
        }

        if (senderState.meetingsLeft == 0) {
            senderState.player.sendMessage(ChatColor.RED + "You have no emergency meetings left!");
            return true;
        }

        return true;
    }
}
