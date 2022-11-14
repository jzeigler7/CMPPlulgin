package jzeigler7.cmpplugin.commands;
import jzeigler7.cmpplugin.BlockRegisteredException;
import jzeigler7.cmpplugin.Bluefier;
import jzeigler7.cmpplugin.CheckInProgressException;
import jzeigler7.cmpplugin.NoReferencePointException;
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
                beginScoring(player);
                stales.clear();
                try {
                    double score = allSourcesScore(startingPoint, player);
                    player.sendMessage(Bluefier.bluefy(player.getName() + "'s current score: " + score + " points"));
                } catch (BlockRegisteredException e) {
                    player.sendMessage(Bluefier.bluefy("Your attempted claim overlaps with " + e.getOwner().getName() + "'s existing claim!"));
                }
            }
        } else {
            player.sendMessage(Bluefier.bluefy("A score check is already in progress! Try again in a few seconds."));
        }
        finishItemScore(player);
        return true;
    }

    /**
     * Removes the sender's entry from the map of ownership registries and clears the current
     * list of blocks to eventually be added to the map of ownership registries.
     * @param player The player to begin the scoring process for
     */
    public static void beginScoring(Player player) {
        recursivePlaceholder.clear();
    }

    /**
     * Concludes the scoring process by adding the list of Locations stored in recursivePlaceholder
     * to the map of ownership registries and allowing /scoreMe to be run again.
     * @param player The player whose scoring process has completed
     */
    public static void finishItemScore(Player player) {
        scoreCheckInProgress = false;
    }

    /**
     * Helper method for the /startCMP command, called upon the end of CMP gameplay.
     * Calculates the given player's score in the same fashion used by the /scoreMe
     * command.
     * @param player The player whose score is being calculated
     * @return The given player's score
     */

    public static double getPlayerScore(Player player) throws CheckInProgressException, NoReferencePointException {
        if (scoreCheckInProgress) {
            throw new CheckInProgressException("A check is already in progress!");
        }
        beginScoring(player);
        scoreCheckInProgress = true;
        Location startingPoint = null;
        startingPoint = referenceCoordinates.get(player);
        if (startingPoint == null) {
            throw new NoReferencePointException(player.getName() + " does not have a reference point set!");
        } else {
            stales.clear();
            double score = -1;
            try {
                score = allSourcesScore(startingPoint, player);
            } finally {
                finishItemScore(player);
                return score;
            }
        }
    }

    /**
     * Determines the score of the player's referenced chest network, held
     * inventory, and Ender Chest
     * @param startingPoint The reference point of the calling player
     * @param player The player being scores
     * @return The sum of the point values in the player's chest network, held
     *         inventory, and Ender Chest inventory
     */
    public static double allSourcesScore(Location startingPoint, Player player) throws BlockRegisteredException {
        double score = scoreFromBlock(startingPoint, startingPoint, player);
        for (ItemStack i: player.getInventory()) {
            if (i != null) {
                score += appraiseItemStack(i);
            }
        }
        for (ItemStack i: player.getEnderChest()) {
            if (i != null) {
                score += appraiseItemStack(i);
            }
        }
        return score;
    }

    /**
     * Determines if the given location is "stale", which is true if it has already been appraised during this
     * call of the scoring process
     * @param toCheck The location to be examined for staleness
     * @return True if the specified location is stale
     */
    public static boolean isStale(Location toCheck) {
        for (Location l: stales) {
            if ((l.getBlockX() == toCheck.getBlockX()) && (l.getBlockY() == toCheck.getBlockY()) && (l.getBlockZ() == toCheck.getBlockZ())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calls the scoreFromBlock method to appraise all six blocks that make direct (non-diagonal) contact with
     * the specified block.
     * @param block The block or storage container whose surroundings should be inspected to detect additional
     *              storage containers to be appraised
     * @param playerRef The reference point of the player being scored
     * @param player The player being scored
     * @return The sum of the point values held in all six adjacent blocks and all such adjacent blocks to those
     * blocks, and so on until all contiguous storage containers have been evaluated
     */
    public static double adjRec(Location block, Location playerRef, Player player) throws BlockRegisteredException {
        double sum = 0;
        stales.add(block);
        Location up = new Location(block.getWorld(), block.getBlockX(), block.getBlockY() + 1, block.getBlockZ());
        Location down = new Location(block.getWorld(), block.getBlockX(), block.getBlockY() - 1, block.getBlockZ());
        Location right = new Location(block.getWorld(), block.getBlockX() + 1, block.getBlockY(), block.getBlockZ());
        Location left = new Location(block.getWorld(), block.getBlockX() - 1, block.getBlockY(), block.getBlockZ());
        Location forward = new Location(block.getWorld(), block.getBlockX(), block.getBlockY(), block.getBlockZ() + 1);
        Location backward = new Location(block.getWorld(), block.getBlockX(), block.getBlockY(), block.getBlockZ() - 1);
        Location[] adjacents = new Location[]{ up, down, right, left, forward, backward, block };
        for (Location l: adjacents) {
            sum += (scoreFromBlock(l, playerRef, player));
        }
        return sum;
    }

    /**
     * Begins by checking if the block in question has already been registered under a different competitor's
     * name. If not and the block in question is not stale, one of three possible scenarios run: If the given
     * block is a storage container (chest, trapped chest, barrel, or Shulker box), it is appraised, and its
     * point value is added to the point values of all non-stale adjacent blocks, whose point values are added
     * to the point values of all of their non-stale adjacent blocks, and so on. If the given block is not a
     * storage container, but it is the player's reference point, the previous algorithm runs with the
     * modification that the reference point is not scored.
     * @param block The block to be examined
     * @param playerRef The reference point of the player to be scored
     * @param player The player being scored
     * @return The sum of the appraised value of the given block and the values of its six neighbors
     */
    public static double scoreFromBlock(@NotNull Location block, Location playerRef, Player player) throws BlockRegisteredException {
        double sum = 0;
        if (!isStale(block)) {
            boolean isBlockPlayerRef = block.equals(playerRef);
            if (isStorageContainer(block)) {
                sum += (appraiseContainer(block) + adjRec(block, playerRef, player));
            } else if (isBlockPlayerRef) {
                sum += adjRec(block, playerRef, player);
            }
        }
        return sum;
    }

    /**
     * Determines if a passed-in block is a storage container.
     * @param block The block to be evaluated
     * @return True if the evaluated block is a chest, trapped chest, barrel, or a Shulker Box
     *         of any color
     */
    public static boolean isStorageContainer(Location block) {
        Material type = block.getBlock().getType();
        return ((type == CHEST) || (type == BARREL) || (type == TRAPPED_CHEST) || isShulkerBox(block.getBlock().getType()));
    }

    /**
     * Determines if a passed-in block is a Shulker Box.
     * @param material The material to be evaluated
     * @return True if the evaluated block is a Shulker Box
     *         of any color
     */
    public static boolean isShulkerBox(Material material) {
        return (material == WHITE_SHULKER_BOX) || (material == LIGHT_GRAY_SHULKER_BOX) ||
                (material == GRAY_SHULKER_BOX) || (material == BLACK_SHULKER_BOX) ||
                (material == BROWN_SHULKER_BOX) || (material == RED_SHULKER_BOX) ||
                (material == ORANGE_SHULKER_BOX) || (material == YELLOW_SHULKER_BOX) ||
                (material == LIME_SHULKER_BOX) || (material == GREEN_SHULKER_BOX) ||
                (material == CYAN_SHULKER_BOX) || (material == LIGHT_BLUE_SHULKER_BOX) ||
                (material == BLUE_SHULKER_BOX) || (material == PURPLE_SHULKER_BOX) ||
                (material == MAGENTA_SHULKER_BOX) || (material == PINK_SHULKER_BOX) ||
                (material == SHULKER_BOX);
    }

    /**
     * Scores an individual storage container
     * @param block The block containing the storage container to be appraised
     * @return The total point value of all items within the container
     */
    public static double appraiseContainer(@NotNull Location block) {
        recursivePlaceholder.add(block);
        Inventory containerInventory = null;
        if (!isStorageContainer(block)) {
            return 0;
        }
        if (block.getBlock().getType() ==  CHEST || block.getBlock().getType() ==  TRAPPED_CHEST) {
            Chest chest = (Chest) (block.getBlock().getState());
            containerInventory = chest.getBlockInventory();
        } else if (block.getBlock().getType() ==  BARREL){
            Barrel barrel = (Barrel) (block.getBlock().getState());
            containerInventory = barrel.getSnapshotInventory();
        } else {
            ShulkerBox shulkerBox = (ShulkerBox) (block.getBlock().getState());
            containerInventory = shulkerBox.getSnapshotInventory();
        }
        double chestTotal = 0;
        for (ItemStack i: containerInventory) {
            if (i != null) {
                chestTotal += appraiseItemStack(i);
            }
        }
        return chestTotal;
    }

    /**
     * Scores an individual Shulker box located within the inventory of another storage
     * container
     * @param shulkerBox The Shulker box to be scored
     * @return The total value of the Shulker box itself and all of its contents
     */
    public static double appraiseShulkerBox(@NotNull ItemStack shulkerBox) {
        double baseValue = 4004.0;
        BlockStateMeta im = (BlockStateMeta)shulkerBox.getItemMeta();
        assert im != null;
        ShulkerBox shulker = (ShulkerBox) im.getBlockState();
        for (ItemStack item: shulker.getInventory().getContents()) {
            if (item != null) {
                baseValue += appraiseItemStack(item);
            }
        }
        if (!(shulkerBox.getType() == SHULKER_BOX)) {
            baseValue += 0.5;
        }
        return baseValue;
    }

    /**
     * Appraises a stack of items within a storage container
     * @param itemStack The stack of items to be appraised
     * @return The total point value of the stack of items
     */
    public static double appraiseItemStack(ItemStack itemStack) {
        Material itemMaterial = itemStack.getType();
        if (isShulkerBox(itemMaterial)) {
            return appraiseShulkerBox(itemStack);
        } else if (pointsMap.keySet().contains(itemMaterial)) {
            return pointsMap.get(itemMaterial) * itemStack.getAmount();
        } else {
            return 0;
        }
    }
}