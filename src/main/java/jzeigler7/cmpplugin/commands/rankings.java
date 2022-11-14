package jzeigler7.cmpplugin.commands;
import jzeigler7.cmpplugin.BinaryHeap;
import jzeigler7.cmpplugin.Bluefier;
import jzeigler7.cmpplugin.Scorer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;

public class rankings implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        HashMap<Double, ArrayList<Player>> scoresToPlayers = Scorer.playerScoreMap();
        BinaryHeap<Double> scoreMaxHeap = new BinaryHeap<>(scoresToPlayers.keySet().toArray(new Double[scoresToPlayers.keySet().size()]), false);
        double currScore = 0;
        String currPlayerString = null;
        if (scoreMaxHeap.length() == 0) {
            sender.sendMessage(Bluefier.bluefy("There are no scores to rank players by!"));
        } else {
            int rank = 1;
            String rankPlayer = null;
            while (scoreMaxHeap.length() > 0) {
                currScore = scoreMaxHeap.remove();
                if (scoresToPlayers.get(currScore).size() == 1) {
                    currPlayerString = scoresToPlayers.get(currScore).get(0).getName();
                } else {
                    currPlayerString = Scorer.getTieString(currScore, scoresToPlayers);
                }
                rankPlayer = ("Rank " + rank + ": " + currPlayerString + ", " + currScore);
                if (rank == 1) {
                    sender.sendMessage(Bluefier.goldify(rankPlayer));
                } else if (rank == 2) {
                    sender.sendMessage(Bluefier.silverfy(rankPlayer));
                } else {
                    sender.sendMessage(Bluefier.bronzify(rankPlayer));
                }
                rank++;
            }
        }
        return false;
    }
}
