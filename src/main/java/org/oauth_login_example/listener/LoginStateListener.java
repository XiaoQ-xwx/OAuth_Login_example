package org.oauth_login_example.listener;

import org.linuxdo.oauthlink.api.OAuthLinkAPI;
import org.linuxdo.oauthlink.event.PlayerOAuthSuccessEvent;
import org.linuxdo.oauthlink.event.PlayerOAuthUnlinkEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.oauth_login_example.restriction.RestrictionAction;

import java.util.List;
import java.util.logging.Level;

/**
 * Manages the lifecycle of restrictions based on OAuth state changes.
 * Applies restrictions on join (if unlinked) and OAuth unlink events,
 * removes restrictions on OAuth success and player quit.
 */
public final class LoginStateListener implements Listener {

    private final Plugin plugin;
    private final List<RestrictionAction> restrictions;

    public LoginStateListener(Plugin plugin, List<RestrictionAction> restrictions) {
        this.plugin = plugin;
        this.restrictions = restrictions;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!OAuthLinkAPI.isLinked(player.getUniqueId())) {
            for (RestrictionAction r : restrictions) {
                r.apply(player);
            }
            plugin.getLogger().log(Level.INFO,
                    "Applied restrictions to unlinked player: {0} ({1})",
                    new Object[]{player.getName(), player.getUniqueId()});
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (RestrictionAction r : restrictions) {
            r.remove(player);
        }
    }

    @EventHandler
    public void onOAuthSuccess(PlayerOAuthSuccessEvent event) {
        Player player = Bukkit.getPlayer(event.getAccount().playerId());
        if (player != null && player.isOnline()) {
            for (RestrictionAction r : restrictions) {
                r.remove(player);
            }
            player.sendMessage("§a你的 LinuxDO 账号已绑定成功！限制已解除。");
            plugin.getLogger().log(Level.INFO,
                    "Restrictions removed for linked player: {0} ({1})",
                    new Object[]{player.getName(), player.getUniqueId()});
        }
    }

    @EventHandler
    public void onOAuthUnlink(PlayerOAuthUnlinkEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayerId());
        if (player != null && player.isOnline()) {
            for (RestrictionAction r : restrictions) {
                r.apply(player);
            }
            player.sendMessage("§c你的 LinuxDO 账号已解绑！移动和交互限制已恢复。");
            plugin.getLogger().log(Level.INFO,
                    "Restrictions re-applied for unlinked player: {0} ({1})",
                    new Object[]{event.getPlayerName(), event.getPlayerId()});
        }
    }
}
