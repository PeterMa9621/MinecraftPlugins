package com.peter.worldBoss.manager;

import com.peter.worldBoss.WorldBoss;
import com.peter.worldBoss.model.BossGroup;
import com.peter.worldBoss.model.BossGroupSetting;
import com.peter.worldBoss.model.BossPlayer;
import com.peter.worldBoss.util.BungeecordUtil;
import com.peter.worldBoss.util.GroupResponse;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.UUID;

public class BossGroupManager {
    public static HashMap<String, BossGroupSetting> bossGroupSetting = new HashMap<>();
    public static HashMap<String, BossGroup> bossGroups = new HashMap<>();
    public static HashMap<UUID, BossPlayer> bossPlayers = new HashMap<>();
    public static WorldBoss plugin;

    @SuppressWarnings("unchecked")
    public static GroupResponse joinGroup(Player player, BossGroup bossGroup) {
        BossPlayer bossPlayer;
        UUID uniqueId = player.getUniqueId();
        if(bossPlayers.containsKey(uniqueId))
            bossPlayer = bossPlayers.get(uniqueId);
        else {
            bossPlayer = new BossPlayer(bossGroup, player.getUniqueId());
            bossPlayers.put(uniqueId, bossPlayer);
            bossGroup.addPlayer(bossPlayer);
            return GroupResponse.canJoin;
        }
        if(bossPlayer.getGroup()!=null && !bossPlayer.getGroup().equals(bossGroup)) {
            return GroupResponse.cannotJoin;
        } else if(bossPlayer.getGroup()!=null && bossPlayer.getGroup().equals(bossGroup)) {
            leaveGroup(player);
            return GroupResponse.joinSameGroup;
        } else {
            bossPlayer.setGroup(bossGroup);
            bossGroup.addPlayer(bossPlayer);
            return GroupResponse.canJoin;
        }
    }

    public static void leaveGroup(Player player) {
        UUID uniqueId = player.getUniqueId();
        if(bossPlayers.containsKey(uniqueId)) {
            BossPlayer bossPlayer = bossPlayers.get(uniqueId);
            BossGroup bossGroup = bossPlayer.getGroup();
            if(bossGroup!=null) {
                bossGroup.removePlayer(bossPlayers.get(uniqueId));
                bossPlayer.setGroup(null);
            }
        }
    }

    public static Boolean containPlayer(Player player, BossGroup bossGroup) {
        UUID uniqueId = player.getUniqueId();
        if(!bossPlayers.containsKey(uniqueId))
            return false;
        BossPlayer bossPlayer = bossPlayers.get(uniqueId);
        return bossPlayer.getGroup()!=null && bossPlayer.getGroup().equals(bossGroup);
    }

    public static void removePlayer(Player player) {
        UUID uniqueId = player.getUniqueId();
        bossPlayers.remove(uniqueId);
    }
}
