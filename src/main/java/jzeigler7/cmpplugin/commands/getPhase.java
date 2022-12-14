package jzeigler7.cmpplugin.commands;
import jzeigler7.cmpplugin.CMPPlugin;
import jzeigler7.cmpplugin.gamePhase;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 *
 * Implements the /getPhase command, which tells the sender the current phase
 * of CMP gameplay.
 *
 * @author Jacob Zeigler
 * @version 1.0
 */
public class getPhase implements CommandExecutor, Listener {
    /**
     * Runs when the /getPhase command is executed
     * @param sender Source of the command
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     * @return true always
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
            if (command.getName().equalsIgnoreCase("getPhase")) {
                switch (CMPPlugin.currentPhase) {
                    case NONE:
                        sender.sendMessage("Game is not in progress.");
                        break;
                    case BEGINNING:
                        sender.sendMessage("Phase 1 in progress.");
                        break;
                    case BREAK:
                        sender.sendMessage("Break in progress.");
                        break;
                    case MIDDLE:
                        sender.sendMessage("Phase 2 in progress.");
                        break;
                    case END:
                        sender.sendMessage("Phase 3 in progress.");
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + CMPPlugin.currentPhase);
                }
            }
            return true;
        }
    }