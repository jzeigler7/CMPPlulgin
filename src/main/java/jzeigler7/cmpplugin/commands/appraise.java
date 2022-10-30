package jzeigler7.cmpplugin.commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 *
 * Implements the /appraise command, which returns the per-unit point
 * value of the item type in the calling player's hand.
 *
 * @author Jacob Zeigler
 * @version 1.0
 */
public class appraise implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        sender.sendMessage("The total value of your currently held item stack is: " + scoreMe.appraiseItemStack(player.getInventory().getItemInMainHand()) + "points");
        return false;
    }
}