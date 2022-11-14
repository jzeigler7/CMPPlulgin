package jzeigler7.cmpplugin.commands;

import jzeigler7.cmpplugin.BlockRegisteredException;
import jzeigler7.cmpplugin.Bluefier;
import jzeigler7.cmpplugin.Scorer;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static jzeigler7.cmpplugin.CMPPlugin.*;

/**
 *
 * Implements the /scoreMe command, which calculates the sender's current score.
 * Also provides a helper method for calculating end-of-game scores.
 *
 * @author Jacob Zeigler
 * @version 1.0
 */
public class scoreMe implements CommandExecutor {
    /**
     * Runs when the /scoreMe command is executed
     * @param sender Source of the command
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     * @return true always
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (!scoreCheckInProgress) {
            scoreCheckInProgress = true;
            Location startingPoint = referenceCoordinates.get(player);
            if (startingPoint == null) {
                player.sendMessage(Bluefier.bluefy("You do not have a reference point set!"));
                player.sendMessage(Bluefier.bluefy("Call /setref when standing next to a chest to set your reference point!"));
            } else {
                Scorer.beginScoring(player);
                stales.clear();
                try {
                    double score = Scorer.allSourcesScore(startingPoint, player);
                    player.sendMessage(Bluefier.bluefy(player.getName() + "'s current score: " + score + " points"));
                } catch (BlockRegisteredException e) {
                    player.sendMessage(Bluefier.bluefy("Your attempted claim overlaps with " + e.getOwner().getName() + "'s existing claim!"));
                }
            }
        } else {
            player.sendMessage(Bluefier.bluefy("A score check is already in progress! Try again in a few seconds."));
        }
        Scorer.finishItemScore(player);
        return true;
    }


}