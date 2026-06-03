package org.oauth_login_example.restriction;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Restricts player movement for unlinked players.
 * Registers a PlayerMoveEvent listener that cancels movement for restricted players.
 */
public final class MoveRestriction implements RestrictionAction, Listener {

    private final Plugin plugin;
    private final Set<UUID> restrictedPlayers = ConcurrentHashMap.newKeySet();

    public MoveRestriction(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void apply(Player player) {
        restrictedPlayers.add(player.getUniqueId());
    }

    @Override
    public void remove(Player player) {
        restrictedPlayers.remove(player.getUniqueId());
    }

    @Override
    public String getDescription() {
        return "MoveRestriction — 禁止未绑定玩家移动";
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (restrictedPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§c你必须先绑定 LinuxDO 账号才能移动！");
        }
    }
}
