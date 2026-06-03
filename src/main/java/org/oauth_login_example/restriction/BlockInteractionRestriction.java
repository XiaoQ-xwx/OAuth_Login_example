package org.oauth_login_example.restriction;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Restricts block breaking and placing for unlinked players.
 * Registers BlockBreakEvent and BlockPlaceEvent listeners.
 */
public final class BlockInteractionRestriction implements RestrictionAction, Listener {

    private final Plugin plugin;
    private final Set<UUID> restrictedPlayers = ConcurrentHashMap.newKeySet();

    public BlockInteractionRestriction(Plugin plugin) {
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
        return "BlockInteractionRestriction — 禁止未绑定玩家破坏/放置方块";
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (restrictedPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§c你必须先绑定 LinuxDO 账号才能交互方块！");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (restrictedPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§c你必须先绑定 LinuxDO 账号才能交互方块！");
        }
    }
}
