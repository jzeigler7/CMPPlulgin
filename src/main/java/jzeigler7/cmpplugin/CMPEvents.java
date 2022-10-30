package jzeigler7.cmpplugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
/**
 *
 * Serves to prevent a player from taking damage from other players if the current phase
 * of CMP gameplay prohibits PVP. Also sends a series of messages describing the current
 * phase of gameplay to any player who joins the server.
 *
 * @author Jacob Zeigler
 * @version 1.0
 */
public class CMPEvents implements Listener {
    /**
     * Runs when a player is damaged. If the current CMP phase is one or three and the
     * source of the damage is another player, that damage is undone
     * @param event The heard damage event
     */
    @EventHandler
    public void onTestEntityDamage(EntityDamageByEntityEvent event) {
        if ((event.getDamager() instanceof Player) && ((CMPPlugin.getCurrentPhase() == gamePhase.BEGINNING) || (CMPPlugin.getCurrentPhase() == gamePhase.END))) {
            if (event.getEntity() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Runs when a player joins the server. If CMP gameplay is active, a message describing
     * the current phase of gameplay is broadcast
     * @param event The heard join event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joinedPlayer = event.getPlayer();
        if (CMPPlugin.registries.keySet().contains(joinedPlayer)) {
            CMPPlugin.registries.put(joinedPlayer, new ArrayList<Location>());
        }
        switch (CMPPlugin.currentPhase) {
            case BEGINNING:
                sendPlayerMessageLater("Phase 1 is active! PVP is disabled.", joinedPlayer, 0);
                sendPlayerMessageLater("Build a well-hidden home and set traps for invaders!", joinedPlayer, 1);
                sendPlayerMessageLater("Begin obtaining resources and progressing through the dimensions!", joinedPlayer, 2);
                sendPlayerMessageLater("Be sure to mark all chests to be scored from with a sign containing your username.", joinedPlayer, 3);
                break;
            case MIDDLE:
                sendPlayerMessageLater("Phase 2 is active! PVP is now enabled. No rules apply!", joinedPlayer, 0);
                sendPlayerMessageLater("Attack other players' bases for resources and defend your own!", joinedPlayer, 1);
                sendPlayerMessageLater("Accumulate as many points as possible in your marked chests!", joinedPlayer, 2);
                sendPlayerMessageLater("Obtain the Dragon Egg to receive millions of points.", joinedPlayer, 3);
                break;
            case END:
                sendPlayerMessageLater("Phase 3 is active! PVP is disabled.", joinedPlayer, 0);
                sendPlayerMessageLater("Fill your chests with as much stuff as possible! Dismantle your home for extra points!", joinedPlayer, 1);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + CMPPlugin.currentPhase);
        };

        if (CMPPlugin.getCurrentPhase() == gamePhase.BREAK) {
            joinedPlayer.kickPlayer("The break period is not over yet.");
        }
    }

    /**
     * Broadcasts a given message to the server after a given number of seconds passes
     * @param message The message to be sent
     * @param seconds The number of seconds that the message is to be delayed by
     */
    public void sendPlayerMessageLater(String message, Player recipient, int seconds) {
        runMethodLater(() -> recipient.sendMessage(message), seconds);
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