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
package de.erethon.dungeonsxl.sign.message;

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.sign.DSignType;
import de.erethon.dungeonsxl.sign.DSignTypeDefault;
import de.erethon.dungeonsxl.sign.PerPlayerSign;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class ActionBarSign extends PerPlayerSign {

    private String text;

    public ActionBarSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
    }

    /* Getters and setters*/
    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /* Actions */
    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        text = lines[1];
        text += lines[2];
        getSign().getBlock().setType(VanillaItem.AIR.getMaterial());
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        if (!super.onPlayerTrigger(player)) {
            return false;
        }
        MessageUtil.sendActionBarMessage(player, text);
        return true;
    }

    @Override
    public DSignType getType() {
        return DSignTypeDefault.ACTION_BAR;
    }

}
