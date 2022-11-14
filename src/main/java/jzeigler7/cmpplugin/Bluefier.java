package jzeigler7.cmpplugin;
import org.bukkit.ChatColor;

/**
 *
 * Implements the bluefy method, which converts a passed-in String into a BLUE string
 *
 * @author Jacob Zeigler
 * @version 1.0
 */
public class Bluefier {
    /**
     * Turns a given message into a blue variant of itself
     * @param inputStr The string to be bluefied
     * @return The bluefied string
     */
    public static String bluefy(String inputStr) {
        return ChatColor.AQUA +inputStr;
    }
    public static String goldify(String inputStr) {
        return ChatColor.GOLD +inputStr;
    }
    public static String silverfy(String inputStr) {
        return ChatColor.GRAY +inputStr;
    }
    public static String bronzify(String inputStr) {
        return ChatColor.DARK_RED +inputStr;
    }
}
