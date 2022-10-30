package jzeigler7.cmpplugin.commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import static jzeigler7.cmpplugin.CMPPlugin.referenceCoordinates;

/**
 *
 * Implements the /setRef command, which allows a player to set their reference point to the
 * location they currently occupy. A reference point is used by the scoring algorithm as a
 * starting point; any chests that are directly adjacent (non-diagonal) to the reference
 * point are scored, as well as any chests adjacent in the same way to those chests, and
 * so on until all chests in the formation are appraised.
 *
 * @author Jacob Zeigler
 * @version 1.0
 */
public class setRef implements CommandExecutor {
    /**
     * Runs when the /setRef command is executed. Sets the sender's reference point to their
     * current location
     * @param sender Source of the command
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     * @return True always
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (referenceCoordinates.keySet().contains(player)) {
            referenceCoordinates.replace(player, player.getLocation());
        } else {
            referenceCoordinates.put(player, player.getLocation());
        }
        player.sendMessage("Reference point has been updated!");
        return true;
    }
}