package pvpSwitch.model;

import java.util.UUID;

public class PvpPlayer {
    UUID uniqueId;
    Boolean canPvp;
    Boolean isBanned;

    public PvpPlayer(UUID uniqueId, Boolean can_pvp, Boolean is_banned) {
        this.uniqueId = uniqueId;
        this.canPvp = can_pvp;
        this.isBanned = is_banned;
    }

    public Boolean canPvp(){
        return this.canPvp;
    }

    public void setPvp(Boolean canPvp){
        this.canPvp = canPvp;
    }

    public void setBanned(Boolean isBanned){
        this.isBanned = isBanned;
    }

    public Boolean isBanned(){
        return this.isBanned;
    }
}
