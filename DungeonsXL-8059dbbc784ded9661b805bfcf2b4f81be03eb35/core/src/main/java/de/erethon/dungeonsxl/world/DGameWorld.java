/*
 * Copyright (C) 2012-2020 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.world;

import de.erethon.caliburn.CaliburnAPI;
import de.erethon.caliburn.item.ExItem;
import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.misc.BlockUtil;
import de.erethon.commons.misc.FileUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.dungeon.Dungeon;
import de.erethon.dungeonsxl.event.gameworld.GameWorldStartGameEvent;
import de.erethon.dungeonsxl.event.gameworld.GameWorldUnloadEvent;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.game.GameRuleProvider;
import de.erethon.dungeonsxl.mob.DMob;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.sign.DSign;
import de.erethon.dungeonsxl.sign.DSignType;
import de.erethon.dungeonsxl.sign.LocationSign;
import de.erethon.dungeonsxl.sign.MobSign;
import de.erethon.dungeonsxl.sign.lobby.StartSign;
import de.erethon.dungeonsxl.trigger.FortuneTrigger;
import de.erethon.dungeonsxl.trigger.ProgressTrigger;
import de.erethon.dungeonsxl.trigger.RedstoneTrigger;
import de.erethon.dungeonsxl.trigger.Trigger;
import de.erethon.dungeonsxl.trigger.TriggerType;
import de.erethon.dungeonsxl.trigger.TriggerTypeDefault;
import de.erethon.dungeonsxl.world.block.GameBlock;
import de.erethon.dungeonsxl.world.block.LockedDoor;
import de.erethon.dungeonsxl.world.block.MultiBlock;
import de.erethon.dungeonsxl.world.block.PlaceableBlock;
import de.erethon.dungeonsxl.world.block.RewardChest;
import de.erethon.dungeonsxl.world.block.TeamBed;
import de.erethon.dungeonsxl.world.block.TeamFlag;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A playable resource instance. There may be any amount of DGameWorlds per DResourceWorld.
 *
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class DGameWorld extends DInstanceWorld {

    public enum Type {
        START_FLOOR,
        END_FLOOR,
        DEFAULT
    }

    private CaliburnAPI caliburn;
    private Game game;

    // Variables
    private Type type = Type.DEFAULT;

    private boolean isPlaying = false;

    // TO DO: Which lists actually need to be CopyOnWriteArrayLists?
    private List<Block> placedBlocks = new LinkedList<>();

    private Set<GameBlock> gameBlocks = new HashSet<>();
    private Set<LockedDoor> lockedDoors = new HashSet<>();
    private Set<PlaceableBlock> placeableBlocks = new HashSet<>();
    private Set<RewardChest> rewardChests = new HashSet<>();
    private Set<TeamBed> teamBeds = new HashSet<>();
    private Set<TeamFlag> teamFlags = new HashSet<>();

    private List<ItemStack> secureObjects = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Sign> classesSigns = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<DMob> dMobs = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<DSign> dSigns = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Trigger> triggers = new CopyOnWriteArrayList<>();

    DGameWorld(DungeonsXL plugin, DResourceWorld resourceWorld, File folder, World world, int id) {
        super(plugin, resourceWorld, folder, world, id);
        caliburn = plugin.getCaliburn();
    }

    DGameWorld(DungeonsXL plugin, DResourceWorld resourceWorld, File folder, int id) {
        this(plugin, resourceWorld, folder, null, id);
        caliburn = plugin.getCaliburn();
    }

    /**
     * @return the Game connected to the DGameWorld
     */
    public Game getGame() {
        if (game == null) {
            for (Game game : plugin.getGameCache()) {
                if (game.getWorld() == this) {
                    this.game = game;
                }
            }
        }

        return game;
    }

    /**
     * @return the type of the floor
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * @return the isPlaying
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * @param isPlaying the isPlaying to set
     */
    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    /**
     * Returns the start location of the respective group.
     *
     * @param dGroup the group
     * @return the start location
     */
    public Location getStartLocation(DGroup dGroup) {
        int index = getGame().getDGroups().indexOf(dGroup);

        // Try the matching location
        for (DSign dSign : dSigns) {
            if (dSign instanceof StartSign) {
                if (((StartSign) dSign).getId() == index) {
                    return ((LocationSign) dSign).getLocation();
                }
            }
        }

        // Try any location
        for (DSign dSign : dSigns) {
            if (dSign instanceof StartSign) {
                return ((LocationSign) dSign).getLocation();
            }
        }

        // Lobby location as fallback
        if (getLobbyLocation() != null) {
            return getLobbyLocation();
        }

        return getWorld().getSpawnLocation();
    }

    /**
     * @return the placeableBlocks
     */
    public Set<GameBlock> getGameBlocks() {
        return gameBlocks;
    }

    /**
     * @param gameBlock the gameBlock to add
     */
    public void addGameBlock(GameBlock gameBlock) {
        gameBlocks.add(gameBlock);

        if (gameBlock instanceof LockedDoor) {
            lockedDoors.add((LockedDoor) gameBlock);
        } else if (gameBlock instanceof PlaceableBlock) {
            placeableBlocks.add((PlaceableBlock) gameBlock);
        } else if (gameBlock instanceof RewardChest) {
            rewardChests.add((RewardChest) gameBlock);
        } else if (gameBlock instanceof TeamBed) {
            teamBeds.add((TeamBed) gameBlock);
        } else if (gameBlock instanceof TeamFlag) {
            teamFlags.add((TeamFlag) gameBlock);
        }
    }

    /**
     * @param gameBlock the gameBlock to remove
     */
    public void removeGameBlock(GameBlock gameBlock) {
        gameBlocks.remove(gameBlock);

        if (gameBlock instanceof LockedDoor) {
            lockedDoors.remove((LockedDoor) gameBlock);
        } else if (gameBlock instanceof PlaceableBlock) {
            placeableBlocks.remove((PlaceableBlock) gameBlock);
        } else if (gameBlock instanceof RewardChest) {
            rewardChests.remove((RewardChest) gameBlock);
        } else if (gameBlock instanceof TeamBed) {
            teamBeds.remove((TeamBed) gameBlock);
        } else if (gameBlock instanceof TeamFlag) {
            teamFlags.remove((TeamFlag) gameBlock);
        }
    }

    /**
     * @return the rewardChests
     */
    public Set<RewardChest> getRewardChests() {
        return rewardChests;
    }

    /**
     * @return the locked doors
     */
    public Set<LockedDoor> getLockedDoors() {
        return lockedDoors;
    }

    /**
     * @return the placeable blocks
     */
    public Set<PlaceableBlock> getPlaceableBlocks() {
        return placeableBlocks;
    }

    /**
     * @return the team beds
     */
    public Set<TeamBed> getTeamBeds() {
        return teamBeds;
    }

    /**
     * @return the team flags
     */
    public Set<TeamFlag> getTeamFlags() {
        return teamFlags;
    }

    /**
     * @return the secureObjects
     */
    public List<ItemStack> getSecureObjects() {
        return secureObjects;
    }

    /**
     * @param secureObjects the secureObjects to set
     */
    public void setSecureObjects(List<ItemStack> secureObjects) {
        this.secureObjects = secureObjects;
    }

    /**
     * @return the classes signs
     */
    public CopyOnWriteArrayList<Sign> getClassesSigns() {
        return classesSigns;
    }

    /**
     * @param signs the classes signs to set
     */
    public void setClasses(CopyOnWriteArrayList<Sign> signs) {
        classesSigns = signs;
    }

    /**
     * @return the dMobs
     */
    public CopyOnWriteArrayList<DMob> getDMobs() {
        return dMobs;
    }

    /**
     * @param dMob the dMob to add
     */
    public void addDMob(DMob dMob) {
        dMobs.add(dMob);
    }

    /**
     * @param dMob the dMob to remove
     */
    public void removeDMob(DMob dMob) {
        dMobs.remove(dMob);
    }

    /**
     * @return the dSigns
     */
    public CopyOnWriteArrayList<DSign> getDSigns() {
        return dSigns;
    }

    /**
     * @param type the DSignType to filter
     * @return the triggers with the type
     */
    public List<DSign> getDSigns(DSignType type) {
        List<DSign> dSignsOfType = new ArrayList<>();
        for (DSign dSign : dSigns) {
            if (dSign.getType() == type) {
                dSignsOfType.add(dSign);
            }
        }
        return dSignsOfType;
    }

    /**
     * @param dSigns the dSigns to set
     */
    public void setDSigns(CopyOnWriteArrayList<DSign> dSigns) {
        this.dSigns = dSigns;
    }

    /**
     * @return the triggers
     */
    public CopyOnWriteArrayList<Trigger> getTriggers() {
        return triggers;
    }

    /**
     * @param type the type to filter
     * @return the triggers with the type
     */
    public List<Trigger> getTriggers(TriggerType type) {
        List<Trigger> triggersOfType = new ArrayList<>();
        for (Trigger trigger : triggers) {
            if (trigger.getType() == type) {
                triggersOfType.add(trigger);
            }
        }
        return triggersOfType;
    }

    /**
     * @param trigger the trigger to add
     */
    public void addTrigger(Trigger trigger) {
        triggers.add(trigger);
    }

    /**
     * @param trigger the trigger to remove
     */
    public void removeTrigger(Trigger trigger) {
        triggers.remove(trigger);
    }

    /**
     * @return the potential amount of mobs in the world
     */
    public int getMobCount() {
        int mobCount = 0;

        signs:
        for (DSign dSign : dSigns) {
            if (!(dSign instanceof MobSign)) {
                continue;
            }

            for (Trigger trigger : dSign.getTriggers()) {
                if (trigger.getType() == TriggerTypeDefault.PROGRESS) {
                    if (((ProgressTrigger) trigger).getFloorCount() > getGame().getFloorCount()) {
                        break signs;
                    }
                }
            }

            mobCount += ((MobSign) dSign).getInitialAmount();
        }

        return mobCount;
    }

    /**
     * @return the Dungeon that contains the DGameWorld
     */
    public Dungeon getDungeon() {
        for (Dungeon dungeon : plugin.getDungeonCache().getDungeons()) {
            if (dungeon.getConfig().containsFloor(getResource())) {
                return dungeon;
            }
        }

        return null;
    }

    /**
     * Set up the instance for the game
     */
    public void startGame() {
        GameWorldStartGameEvent event = new GameWorldStartGameEvent(this, getGame());
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        isPlaying = true;

        for (DSign dSign : dSigns) {
            if (dSign != null) {
                if (!dSign.getType().isOnDungeonInit()) {
                    dSign.onInit();
                }
            }
        }

        for (Trigger trigger : getTriggers(TriggerTypeDefault.REDSTONE)) {
            ((RedstoneTrigger) trigger).onTrigger();
        }

        for (Trigger trigger : getTriggers(TriggerTypeDefault.FORTUNE)) {
            ((FortuneTrigger) trigger).onTrigger();
        }

        for (DSign dSign : dSigns) {
            if (dSign != null && !dSign.isErroneous() && !dSign.hasTriggers()) {
                dSign.onTrigger();
            }
        }
    }

    /**
     * Delete this instance.
     */
    @Override
    public void delete() {
        GameWorldUnloadEvent event = new GameWorldUnloadEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        kickAllPlayers();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.unloadWorld(getWorld(), true);
            FileUtil.removeDir(getFolder());
            worlds.removeInstance(this);
        }, 20);
    }

    /**
     * Handles what happens when a player breaks a block.
     *
     * @param event the passed Bukkit event
     * @return if the event is cancelled
     */
    public boolean onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        for (DSign dSign : dSigns) {
            if (dSign == null) {
                continue;
            }
            if ((block.equals(dSign.getSign().getBlock()) || block.equals(BlockUtil.getAttachedBlock(dSign.getSign().getBlock()))) && dSign.getType().isProtected()) {
                return true;
            }
        }

        for (GameBlock gameBlock : gameBlocks) {
            if (block.equals(gameBlock.getBlock())) {
                if (gameBlock.onBreak(event)) {
                    return true;
                }

            } else if (gameBlock instanceof MultiBlock) {
                if (block.equals(((MultiBlock) gameBlock).getAttachedBlock())) {
                    if (gameBlock.onBreak(event)) {
                        return true;
                    }
                }
            }
        }

        Game game = getGame();
        if (game == null) {
            return true;
        }

        GameRuleProvider rules = game.getRules();
        if (!rules.canBreakBlocks() && !rules.canBreakPlacedBlocks()) {
            return true;
        }

        // Cancel if a protected entity is attached
        for (Entity entity : world.getNearbyEntities(block.getLocation(), 2, 2, 2)) {
            if (!(entity instanceof Hanging)) {
                continue;
            }
            if (entity.getLocation().getBlock().getRelative(((Hanging) entity).getAttachedFace()).equals(block)) {
                Hanging hanging = (Hanging) entity;
                if (rules.getDamageProtectedEntities().contains(caliburn.getExMob(hanging))) {
                    event.setCancelled(true);
                    break;
                }
            }
        }

        Map<ExItem, HashSet<ExItem>> whitelist = rules.getBreakWhitelist();
        ExItem material = VanillaItem.get(block.getType());
        ExItem breakTool = caliburn.getExItem(player.getItemInHand());

        if (rules.canBreakPlacedBlocks() && placedBlocks.contains(block)) {
            return false;
        }
        if (whitelist != null && whitelist.containsKey(material)
                && (whitelist.get(material) == null
                || whitelist.get(material).isEmpty()
                || whitelist.get(material).contains(breakTool))) {
            return false;
        }

        return true;
    }

    /**
     * Handles what happens when a player places a block.
     *
     * @param player
     * @param block
     * @param against
     * @param hand    the event parameters.
     * @return if the event is cancelled
     */
    public boolean onPlace(Player player, Block block, Block against, ItemStack hand) {
        Game game = getGame();
        if (game == null) {
            return true;
        }

        GameRuleProvider rules = game.getRules();

        PlaceableBlock placeableBlock = null;
        for (PlaceableBlock gamePlaceableBlock : placeableBlocks) {
            if (gamePlaceableBlock.canPlace(block, caliburn.getExItem(hand))) {
                placeableBlock = gamePlaceableBlock;
                break;
            }
        }
        if (!rules.canPlaceBlocks() && placeableBlock == null) {
            // Workaround for a bug that would allow 3-Block-high jumping
            Location loc = player.getLocation();
            if (loc.getY() > block.getY() + 1.0 && loc.getY() <= block.getY() + 1.5) {
                if (loc.getX() >= block.getX() - 0.3 && loc.getX() <= block.getX() + 1.3) {
                    if (loc.getZ() >= block.getZ() - 0.3 && loc.getZ() <= block.getZ() + 1.3) {
                        loc.setX(block.getX() + 0.5);
                        loc.setY(block.getY());
                        loc.setZ(block.getZ() + 0.5);
                        player.teleport(loc);
                    }
                }
            }

            return true;
        }
        if (placeableBlock != null) {
            placeableBlock.onPlace();
        }

        Set<ExItem> whitelist = rules.getPlaceWhitelist();
        if (whitelist == null || whitelist.contains(VanillaItem.get(block.getType()))) {
            placedBlocks.add(block);
            return false;
        }

        return true;
    }

    /* Statics */
    /**
     * @param world the instance
     * @return the EditWorld that represents the world
     */
    public static DGameWorld getByWorld(World world) {
        DInstanceWorld instance = DungeonsXL.getInstance().getDWorldCache().getInstanceByName(world.getName());

        if (instance instanceof DGameWorld) {
            return (DGameWorld) instance;

        } else {
            return null;
        }
    }

}
