package dailyQuest.gui;

import dailyQuest.DailyQuest;
import dailyQuest.model.Quest;
import dailyQuest.model.QuestPlayer;
import dailyQuest.config.ConfigManager;
import dailyQuest.manager.QuestManager;
import dailyQuest.util.IconType;
import dailyQuest.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GuiManager {
    DailyQuest plugin;

    public GuiManager(DailyQuest plugin) {
        this.plugin = plugin;
    }

    public Inventory getQuestGUI(Player p, String name)
    {
        Inventory inv = Bukkit.createInventory(p, 9, "§8日常任务");

        ItemStack accept = plugin.configManager.guiIcons.get(IconType.Accept);
        ItemStack giveUp = plugin.configManager.guiIcons.get(IconType.GiveUp);
        ItemStack goBack = plugin.configManager.guiIcons.get(IconType.GoBack);
        ItemStack whatIsDailyQuest = plugin.configManager.guiIcons.get(IconType.WhatIsDailyQuest);
        ItemStack npc = plugin.configManager.guiIcons.get(IconType.NPC);
        Util.replacePlaceholder(npc, name);

        inv.setItem(0, npc);
        inv.setItem(2, accept);
        inv.setItem(3, giveUp);
        inv.setItem(4, whatIsDailyQuest);
        inv.setItem(5, goBack);

        return inv;
    }

    public Inventory createGUI(Player p, String name, int NPCID)
    {
        Inventory inv = Bukkit.createInventory(p, 9, "§8NPC");
        ItemStack finishQuest = plugin.configManager.guiIcons.get(IconType.FinishQuest);
        ItemStack goBack = plugin.configManager.guiIcons.get(IconType.GoBack);

        ItemStack npc = plugin.configManager.guiIcons.get(IconType.NPC);
        Util.replacePlaceholder(npc, name);
        inv.setItem(0, npc);
        QuestPlayer questPlayer = plugin.questPlayerManager.getQuestPlayer(p);
        if(questPlayer.getCurrentNumber()!=0 &&
                QuestManager.quests.get(questPlayer.getWhatTheQuestIs()).getNPCId()==NPCID)
        {
            inv.setItem(0, npc);
            inv.setItem(3, finishQuest);
            inv.setItem(5, goBack);
        }
        else
        {
            inv.setItem(4, goBack);
        }
        return inv;
    }

    public ArrayList<Inventory> questItemGUI(Player p)
    {
        Inventory inv = Bukkit.createInventory(p, 54, "所有任务物品-页数:1");
        ItemStack next = Util.createItem("PAPER", 1, "§3点击进入下一页", null, 0);
        ItemStack previous = Util.createItem("PAPER", 1, "§3点击进入上一页", null, 0);
        inv.setItem(47, previous);
        inv.setItem(51, next);
        ArrayList<Inventory> list = new ArrayList<Inventory>();
        ArrayList<Quest> quests = QuestManager.quests;
        for(int i=0; i<quests.size(); i++) {
            if(quests.get(i).getQuestInfo().getType().equalsIgnoreCase("item")) {
                inv.setItem(i%44, quests.get(i).getQuestInfo().getQuestItem());
            }
            if(i>43 && i%44==0)
            {
                list.add(inv);
                inv = Bukkit.createInventory(p, 54, "所有任务物品-页数:"+(((i+1)/44)+1));
                inv.setItem(47, previous);
                inv.setItem(51, next);
            }
        }

        list.add(inv);

        return list;
    }

    public ArrayList<Inventory> rewardItemGUI(Player p)
    {
        Inventory inv = Bukkit.createInventory(p, 54, "所有奖励物品-页数:1");
        ItemStack next = Util.createItem("PAPER", 1, "§3点击进入下一页", null, 0);
        ItemStack previous = Util.createItem("PAPER", 1, "§3点击进入上一页", null, 0);
        ItemStack interval = Util.createItem("GLASS", 1, " ", null, 0);

        inv.setItem(47, previous);
        inv.setItem(51, next);
        ArrayList<Inventory> list = new ArrayList<Inventory>();
        int index = 0;
        ArrayList<Quest> quests = QuestManager.quests;
        List<String> rewards = plugin.configManager.rewards;
        for(int i=0; i<quests.size(); i++)
        {
            if(quests.get(i).getQuestInfo().getType().equalsIgnoreCase("item"))
            {
                for(String reward: rewards)
                {
                    ItemStack rewardIcon = Util.createItem("DIAMOND", 1, "§3奖励指令为", new ArrayList<String>() {{
                        add(reward);
                    }}, 0);
                    inv.setItem(index%44, rewardIcon);
                    index++;
                    if(index>43 && index%44==0)
                    {
                        list.add(inv);
                        inv = Bukkit.createInventory(p, 54, "所有奖励物品-页数:"+(((index+1)/44)+1));
                        inv.setItem(47, previous);
                        inv.setItem(51, next);
                    }
                }
                inv.setItem(index%44, interval);
                index ++;

            }
            if(index>43 && index%44==0)
            {
                list.add(inv);
                inv = Bukkit.createInventory(p, 54, "所有奖励物品-页数:"+(((index+1)/44)+1));
                inv.setItem(47, previous);
                inv.setItem(51, next);
            }
        }

        list.add(inv);

        return list;
    }
}
