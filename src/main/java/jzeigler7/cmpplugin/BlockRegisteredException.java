package jzeigler7.cmpplugin;
import org.bukkit.entity.Player;

/**
 *
 * This exception is thrown when attempting to score from a block that has already been
 * registered by another player.
 *
 * @author Jacob Zeigler
 * @version 1.0
 */
public class BlockRegisteredException extends Exception {
    /**
     * The player whose existing claim overlaps with the new attempted claim
     */
    private Player owner;
    /**
     * Single-argument constructor for BlockRegisteredException
     * @param errorMessage The message to be displayed
     */
    public BlockRegisteredException(String errorMessage, Player owner) {
        super(errorMessage);
    }

    /**
     * Getter for owner
     * @return
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Setter for owner
     * @param owner The new owner of the existing claim
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }
}