package jzeigler7.cmpplugin.commands;
import jzeigler7.cmpplugin.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import static jzeigler7.cmpplugin.CMPPlugin.*;
/**
 *
 * Implements the /startCMP command, which can only be run by operators.
 * This begins CMP gameplay, which progresses through a series of phases
 * for a specified number of minutes.
 *
 * @author Jacob Zeigler
 * @version 1.0
 */
public class startCMP implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender.isOp()) {
            if (command.getName().equalsIgnoreCase("startCMP")) {
                if (CMPPlugin.gameInProgress) {
                    sender.sendMessage(Bluefier.bluefy("CMP is already in progress!"));
                } else {
                    if (args.length == 4) {
                        sender.sendMessage(Bluefier.bluefy("The game has begun!"));
                        CMPPlugin.gameInProgress = true;
                        CMPPlugin.currentPhase = gamePhase.BEGINNING;
                        sendPhaseOneMessages(args[0]);
                        runMethodLater(() -> {
                            CMPPlugin.currentPhase = gamePhase.BREAK;
                            sendBreakMessages(args[1]);
                            Collection<Player> players = (Collection<Player>) Bukkit.getOnlinePlayers();
                            for (Player player : players) {
                                player.kickPlayer("The break period has begun. Phase 2 will begin in " + args[1] + " minutes.");
                            }
                            runMethodLater(() -> {
                                CMPPlugin.currentPhase = gamePhase.MIDDLE;
                                sendPhaseTwoMessages(args[2]);
                                runMethodLater(() -> {
                                    CMPPlugin.currentPhase = gamePhase.END;
                                    sendPhaseThreeMessages(args[3]);
                                    runMethodLater(() -> {
                                        CMPPlugin.currentPhase = gamePhase.NONE;
                                        CMPPlugin.gameInProgress = false;
                                        sendMessageLater("The game has finished!", 0, "blue");
                                        if (referenceCoordinates.isEmpty()) {
                                            sendMessageLater("There were no scores to count!", 2, "blue");

                                        } else {
                                            HashMap<Double, ArrayList<Player>> scoresToPlayers = Scorer.playerScoreMap();
                                            BinaryHeap<Double> scoreMaxHeap = new BinaryHeap<>(scoresToPlayers.keySet().toArray(new Double[scoresToPlayers.keySet().size()]), false);
                                            double currScore = scoreMaxHeap.remove();
                                            Player currPlayer = null;
                                            int lastWait = 0;
                                            switch (scoresToPlayers.get(currScore).size()) {
                                                case 0:
                                                    sendMessageLater("There were no winners! If this message shows, there was an error in the scoring algorithm!", 3, "blue");
                                                    lastWait = 3;
                                                    break;
                                                case 1:
                                                    currPlayer = scoresToPlayers.get(currScore).get(0);
                                                    sendMessageLater("The winner is...", 3);
                                                    sendMessageLater(currPlayer.getName() + ", with a final score of " + currScore + " points!", 8, "gold");
                                                    lastWait = 8;
                                                    break;
                                                default:
                                                    int numTiedPlayers = scoresToPlayers.get(currScore).size();
                                                    sendMessageLater(("There was a " + numTiedPlayers + "-way tie for first place!"), 3);
                                                    sendMessageLater(("The winners are " +Scorer.getTieString(currScore, scoresToPlayers) + ", with a score of " + currScore + " points!"), 6);
                                                    lastWait = 6;
                                            }
                                            lastWait += 5;
                                            if ((referenceCoordinates.size() == 1)) {
                                                sendMessageLater("They were the only player in the contest!", lastWait);
                                            } else {
                                                sendMessageLater("The runners-up, in descending point order are:", lastWait);
                                                int rank = 2;
                                                while (scoreMaxHeap.length() > 0) {
                                                    currScore = scoreMaxHeap.remove();
                                                    int iterativeWaitTime = ((lastWait) + (3 * rank) - 3);
                                                    switch (scoresToPlayers.get(currScore).size()) {
                                                        case 0:
                                                            sendMessageLater("How did this happen?", iterativeWaitTime);
                                                            break;
                                                        case 1:
                                                            currPlayer = scoresToPlayers.get(currScore).get(0);
                                                            sendMessageLater("Rank " + rank + ": " + currPlayer.getName() +
                                                            " - " + currScore, iterativeWaitTime, (rank == 2) ? "silver":"bronze");
                                                            break;
                                                        default:
                                                            sendMessageLater("Rank " + rank + ": " + Scorer.getTieString(currScore, scoresToPlayers) +
                                                            " - " + currScore, iterativeWaitTime, (rank == 2) ? "silver":"bronze");
                                                    }
                                                    rank++;
                                                }
                                            }

                                        }
                                    }, Integer.parseInt(args[3]) * 60);
                                }, Integer.parseInt(args[2]) * 60);
                            }, Integer.parseInt(args[1]) * 60);
                        }, Integer.parseInt(args[0]) * 60);
                    } else {
                        sender.sendMessage(Bluefier.bronzify("Include arguments!"));
                    }
                }
            }
        } else {
            sender.sendMessage(Bluefier.bronzify("Only operators may execute this command!"));
        }
        return true;
    }



    /**
     * Broadcasts a series of messages describing the first phase of CMP gameplay
     * @param length The length of phase one of CMP
     */
    public void sendPhaseOneMessages(String length) {
        sendMessageLater("Phase 1 is active! PVP is disabled.", 0);
        sendMessageLater("Build a well-hidden home and set traps for invaders!", 1);
        sendMessageLater("Begin obtaining resources and progressing through the dimensions!", 2);
        sendMessageLater("Be sure to mark all chests to be scored from with a sign containing your username.", 3);
        sendMessageLater("Phase 1 will last for " + length + " minutes.", 4);
    }

    /**
     *
     * Broadcasts a series of messages describing the break phase of CMP
     * @param length The length of the break
     */
    public void sendBreakMessages(String length) {
        sendMessageLater("The break period has begun. Phase 2 will begin in " + length + " minutes.", 0);
        sendMessageLater("Log back on after the break!", 1);
    }

    /**
     * Broadcasts a series of messages describing the second phase of CMP gameplay
     * @param length The length of phase two
     */
    public void sendPhaseTwoMessages(String length) {
        sendMessageLater("Phase 2 is active! PVP is now enabled. No rules apply!", 0);
        sendMessageLater("Attack other players' bases for resources and defend your own!", 1);
        sendMessageLater("Accumulate as many points as possible in your marked chests!", 2);
        sendMessageLater("Obtain the Dragon Egg to receive millions of points.", 3);
        sendMessageLater("Phase 2 will last for " + length + " minutes.", 4);
    }

    /**
     * Broadcasts a series of messages describing the third phase of CMP gameplay
     * @param length The length of phase three
     */
    public void sendPhaseThreeMessages(String length) {
        sendMessageLater("Phase 3 is active! PVP is disabled.", 0);
        sendMessageLater("Fill your chests with as much stuff as possible! Dismantle your home for extra points!", 1);
        sendMessageLater("Phase 3 will last for " + length + " minutes.", 2);
    }

    /**
     * Broadcasts a given message to the server after a given number of seconds passes
     * @param message The message to be sent
     * @param seconds The number of seconds that the message is to be delayed by
     */
    public void sendMessageLater(String message, int seconds) {
        runMethodLater(() -> Bukkit.broadcastMessage(message), seconds);
    }

    /**
     * Broadcasts a given message to the server after a given number of seconds passes
     * @param message The message to be sent
     * @param seconds The number of seconds that the message is to be delayed by
     */
    public void sendMessageLater(String message, int seconds, String color) {
        switch (color) {
            case "blue":
                runMethodLater(() -> Bukkit.broadcastMessage(Bluefier.bluefy(message)), seconds);
                break;
            case "gold":
                runMethodLater(() -> Bukkit.broadcastMessage(Bluefier.goldify(message)), seconds);
                break;
            case "silver":
                runMethodLater(() -> Bukkit.broadcastMessage(Bluefier.silverfy(message)), seconds);
                break;
            case "bronze":
                runMethodLater(() -> Bukkit.broadcastMessage(Bluefier.bronzify(message)), seconds);
                break;
            default:
                runMethodLater(() -> Bukkit.broadcastMessage(message), seconds);
        }
        runMethodLater(() -> Bukkit.broadcastMessage(message), seconds);
    }

    /**
     * Runs a given message after a given number of seconds have passed
     * @param method The method to be run at a later point
     * @param seconds The number of seconds that the method is to be delayed by
     */
    public void runMethodLater(Runnable method, int seconds) {
        new BukkitRunnable() {
            @Override
            public void run() {
                method.run();
            }
        }.runTaskLater(CMPPlugin.getPlugin(CMPPlugin.class), seconds * 20L);
    }
}