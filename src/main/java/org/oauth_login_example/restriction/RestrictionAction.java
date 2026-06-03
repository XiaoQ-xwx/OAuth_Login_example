package org.oauth_login_example.restriction;

import org.bukkit.entity.Player;

/**
 * Represents a single restriction action applied to unlinked players.
 * Implement this interface to add new restriction types.
 *
 * <p>Each restriction independently registers its own event listeners
 * and manages its own lifecycle via {@link #apply(Player)} / {@link #remove(Player)}.
 */
public interface RestrictionAction {

    /**
     * Apply this restriction to the given player.
     * Called when a player is detected as unlinked.
     */
    void apply(Player player);

    /**
     * Remove this restriction from the given player.
     * Called when a player links their account or disconnects.
     */
    void remove(Player player);

    /**
     * Human-readable description of this restriction.
     * Used for logging and debugging.
     */
    String getDescription();
}
