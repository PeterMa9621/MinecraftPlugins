package me.elsiff.morefish.fishing.condition

import me.elsiff.morefish.fishing.competition.FishingCompetition
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType

class PotionEffectCondition(
    private val effectType: PotionEffectType,
    private val minAmplifier: Int
) : FishCondition {
    override fun check(caught: Item, fisher: Player, competition: FishingCompetition): Boolean {
        if(fisher.hasPotionEffect(effectType)){
            val potionEffect = fisher.getPotionEffect(effectType)
            if(potionEffect!=null)
                return potionEffect.amplifier >= minAmplifier
        }
        return false
    }
}