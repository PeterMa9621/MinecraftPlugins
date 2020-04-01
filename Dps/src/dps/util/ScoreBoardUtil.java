package dps.util;

import dps.Dps;
import dps.model.DpsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import scoreBoard.ScoreBoard;
import scoreBoard.ScoreBoardAPI;

import java.util.*;

public class ScoreBoardUtil {
    public static void displayDpsBoard(HashMap<UUID, DpsPlayer> dpsPlayers)
    {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective randomObjective = scoreboard.registerNewObjective("scoreboard1", "dummy1");
        String title = "§7总输出排行";

        randomObjective.setDisplayName(title);
        randomObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        int number = dpsPlayers.size();
        if(number>15)
            number = 15;

        HashMap<DpsPlayer, Double> data = new HashMap<>();
        dpsPlayers.forEach((uuid, dpsPlayer) -> data.put(dpsPlayer, dpsPlayer.getDpsScore()));
        List<Map.Entry<DpsPlayer, Double>> list = sort(data);
        int j=0;
        for (Map.Entry<DpsPlayer, Double> mapping : list)
        {
            if(j>14){
                randomObjective.getScore("...").setScore(0);
                break;
            }

            DpsPlayer dpsPlayer = mapping.getKey();
            dpsPlayer.setRank(j+1);
            String content = String.format("§e%s §f- §2%.2f", dpsPlayer.getPlayer().getName(), mapping.getValue());

            randomObjective.getScore(content).setScore(number-j);
            j++;
        }

        for(DpsPlayer dpsPlayer:dpsPlayers.values()){
            dpsPlayer.getPlayer().setScoreboard(scoreboard);
        }
    }

    public static List<Map.Entry<DpsPlayer, Double>> sort(HashMap<DpsPlayer, Double> map)
    {
        //将map.entrySet()转换成list
        List<Map.Entry<DpsPlayer, Double>> list = new ArrayList<Map.Entry<DpsPlayer, Double>>(map.entrySet());
        //降序排序
        list.sort((o1, o2) -> {
            //return o1.getValue().compareTo(o2.getValue());
            return o2.getValue().compareTo(o1.getValue());
        });

        return list;

    }
}
