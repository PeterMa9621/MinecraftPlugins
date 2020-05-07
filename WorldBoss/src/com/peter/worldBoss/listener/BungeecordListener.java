package com.peter.worldBoss.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.peter.worldBoss.WorldBoss;
import com.peter.worldBoss.manager.BossGroupManager;
import com.peter.worldBoss.model.BossGroup;
import com.peter.worldBoss.model.BossGroupSetting;
import com.peter.worldBoss.util.BungeecordUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BungeecordListener implements PluginMessageListener {
    private WorldBoss plugin;
    public BungeecordListener(WorldBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if ( !channel.equalsIgnoreCase(BungeecordUtil.mainChannel) ) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput( bytes );
        String subChannel = in.readUTF();
        if ( subChannel.equalsIgnoreCase( BungeecordUtil.subChannel ) ) {
            String data = in.readUTF();

            JSONParser parser = new JSONParser();
            try {
                JSONObject jsonObject = (JSONObject) parser.parse(data);
                JSONArray uuids = (JSONArray) jsonObject.get("players");
                String groupName = (String) jsonObject.get("groupName");
                BossGroup bossGroup = BossGroupManager.bossGroups.get(groupName);

                for (Object o : uuids) {
                    String uuid = (String) o;
                    Player eachPlayer = Bukkit.getPlayer(UUID.fromString(uuid));
                    if(eachPlayer==null)
                        continue;
                    BossGroupManager.joinGroup(eachPlayer, bossGroup);
                }

                BossGroupSetting setting = BossGroupManager.bossGroupSetting.get(groupName);
                Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                    bossGroup.startGame(setting.getStartGameCmd(), plugin);
                }, 20);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
