package org.oauth_login_example.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /login command — proxies to /linkld to initiate the LinuxDO OAuth flow.
 * Provides a dedicated entry point separate from OAuth_Framework's command.
 */
public class LoginCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                              String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "此命令只能由玩家执行");
            return true;
        }

        // Delegate to OAuth_Framework's /linkld for the full OAuth flow
        player.performCommand("linkld");
        return true;
    }
}
