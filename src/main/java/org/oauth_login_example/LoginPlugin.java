package org.oauth_login_example;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.linuxdo.oauthlink.api.OAuthLinkAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.oauth_login_example.command.LoginCommand;
import org.oauth_login_example.listener.LoginStateListener;
import org.oauth_login_example.restriction.BlockInteractionRestriction;
import org.oauth_login_example.restriction.MoveRestriction;
import org.oauth_login_example.restriction.RestrictionAction;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Main plugin class for OAuth_Login_example.
 * Restricts unlinked players from moving and interacting with blocks.
 * Restrictions are automatically lifted when the player links their LinuxDO account.
 */
public final class LoginPlugin extends JavaPlugin {

    /** 6 seconds = 120 Minecraft ticks (20 ticks/s). */
    private static final long REMINDER_INTERVAL_TICKS = 120L;

    private final List<RestrictionAction> restrictions = new CopyOnWriteArrayList<>();
    private BukkitTask reminderTask;

    @Override
    public void onEnable() {
        // Create restrictions — each self-registers its own event listeners
        MoveRestriction moveRestriction = new MoveRestriction(this);
        BlockInteractionRestriction blockRestriction = new BlockInteractionRestriction(this);

        restrictions.add(moveRestriction);
        restrictions.add(blockRestriction);

        // Register the lifecycle listener that orchestrates restrictions
        getServer().getPluginManager().registerEvents(
                new LoginStateListener(this, restrictions), this);

        // Register /login command
        Objects.requireNonNull(getCommand("login"))
                .setExecutor(new LoginCommand());

        // Apply restrictions to already-online unlinked players (e.g. after /reload)
        for (Player p : getServer().getOnlinePlayers()) {
            if (!OAuthLinkAPI.isLinked(p.getUniqueId())) {
                for (RestrictionAction r : restrictions) {
                    r.apply(p);
                }
            }
        }

        // Start periodic chat reminder for restricted players
        startReminderTask();

        getLogger().info("OAuth_Login_example enabled — "
                + restrictions.size() + " restrictions active, /login registered, "
                + (REMINDER_INTERVAL_TICKS / 20) + "s reminder started");
    }

    @Override
    public void onDisable() {
        // Stop reminder task
        if (reminderTask != null) {
            reminderTask.cancel();
            reminderTask = null;
        }

        // Clean up all restrictions from online players
        for (Player p : getServer().getOnlinePlayers()) {
            for (RestrictionAction r : restrictions) {
                r.remove(p);
            }
        }
        restrictions.clear();
        getLogger().info("OAuth_Login_example disabled");
    }

    // ===== Periodic Reminder =====

    /** Starts a repeating task that reminds unlinked players to use /login. */
    private void startReminderTask() {
        reminderTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : getServer().getOnlinePlayers()) {
                // Only remind players who are still restricted (not yet linked)
                if (!OAuthLinkAPI.isLinked(p.getUniqueId())) {
                    sendReminder(p);
                }
            }
        }, REMINDER_INTERVAL_TICKS, REMINDER_INTERVAL_TICKS);
    }

    /** Sends a clickable chat reminder to a restricted player. */
    private void sendReminder(Player player) {
        ComponentBuilder builder = new ComponentBuilder()
                .append(ChatColor.GOLD + "⚡ ")
                .append(new ComponentBuilder("[点击登录]")
                        .color(net.md_5.bungee.api.ChatColor.AQUA)
                        .bold(true)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/login"))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new Text("绑定 Linux.DO 账号以解除限制")))
                        .create())
                .append(ChatColor.GRAY + " 请绑定 Linux.DO 账号解除移动/交互限制");

        player.spigot().sendMessage(builder.create());
    }
}
