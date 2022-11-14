package jzeigler7.cmpplugin.commands;
import jzeigler7.cmpplugin.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import org.bukkit.block.Chest;
import org.bukkit.block.Barrel;
import org.bukkit.block.ShulkerBox;
import static jzeigler7.cmpplugin.CMPPlugin.*;
import static org.bukkit.Material.*;

/**
 *
 * Implements the /scoreMe command, which calculates the sender's current score.
 * Also provides a helper method for calculating end-of-game scores.
 *
 * @author Jacob Zeigler
 * @version 1.0
 */
public class score implements CommandExecutor {
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
        Player sendPlayer = (Player) sender;
        if (args.length == 0) {
            sendPlayer.sendMessage(Bluefier.bluefy("Indicate which player should be scored!"));
        } else {
            Player subject = null;
            for (Player p: Bukkit.getOnlinePlayers()) {
                if (p.getName().equals(args[0])) {
                    subject = p;
                }
            }
            if (!scoreCheckInProgress) {
                scoreCheckInProgress = true;
                Location startingPoint = referenceCoordinates.get(subject);
                if (startingPoint == null) {
                    sendPlayer.sendMessage(Bluefier.bluefy("This player does not have a reference point set!"));
                } else {
                    Scorer.beginScoring(subject);
                    stales.clear();
                    double score = 0;
                    try {
                        score = Scorer.allSourcesScore(startingPoint, subject);
                    } catch (BlockRegisteredException e) {
                        throw new RuntimeException(e);
                    }
                    sendPlayer.sendMessage(Bluefier.bluefy(subject.getName() + "'s current score: " + score + " points"));
                }
            } else {
                sendPlayer.sendMessage(Bluefier.bluefy("A score check is already in progress! Try again in a few seconds."));
            }
            Scorer.finishItemScore(sendPlayer);
        }
        return true;
    }
}
