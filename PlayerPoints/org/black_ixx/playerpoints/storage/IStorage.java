package org.black_ixx.playerpoints.storage;

import java.util.Collection;

public abstract interface IStorage
{
  public abstract int getPoints(String paramString);
  
  public abstract boolean setPoints(String paramString, int paramInt);
  
  public abstract boolean playerEntryExists(String paramString);
  
  public abstract boolean removePlayer(String paramString);
  
  public abstract boolean destroy();
  
  public abstract boolean build();
  
  public abstract Collection<String> getPlayers();
}


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\storage\IStorage.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */