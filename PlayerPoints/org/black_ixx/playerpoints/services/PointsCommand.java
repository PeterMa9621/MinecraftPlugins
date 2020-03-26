package org.black_ixx.playerpoints.services;

import java.util.EnumMap;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.models.Flag;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract interface PointsCommand
{
  public abstract boolean execute(PlayerPoints paramPlayerPoints, CommandSender paramCommandSender, Command paramCommand, String paramString, String[] paramArrayOfString, EnumMap<Flag, String> paramEnumMap);
}


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\services\PointsCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */