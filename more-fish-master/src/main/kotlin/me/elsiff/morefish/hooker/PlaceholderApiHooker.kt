package me.elsiff.morefish.hooker

import me.clip.placeholderapi.PlaceholderAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.elsiff.morefish.MoreFish
import me.elsiff.morefish.configuration.format.Format
import me.elsiff.morefish.fishing.competition.FishingCompetition
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * Created by elsiff on 2019-01-24.
 */
class PlaceholderApiHooker : PluginHooker {
    override val pluginName = "PlaceholderAPI"
    override var hasHooked = false

    override fun hook(plugin: MoreFish) {
        MoreFishPlaceholder(plugin).register();
        Format.init(this)
        hasHooked = true
    }

    fun tryReplacing(string: String, player: Player? = null): String {
        return PlaceholderAPI.setPlaceholders(player, string)
    }

    class MoreFishPlaceholder(var plugin: MoreFish) : PlaceholderExpansion() {

        /**
         * This method should always return true unless we
         * have a dependency we need to make sure is on the server
         * for our placeholders to work!
         *
         * @return always true since we do not have any dependencies.
         */
        override fun canRegister(): Boolean {
            return true
        }

        /**
         * The name of the person who created this expansion should go here.
         *
         * @return The name of the author as a String.
         */
        override fun getAuthor(): String? {
            return "elsiff"
        }

        /**
         * The placeholder identifier should go here.
         * <br></br>This is what tells PlaceholderAPI to call our onRequest
         * method to obtain a value if a placeholder starts with our
         * identifier.
         * <br></br>This must be unique and can not contain % or _
         *
         * @return The identifier in `%<identifier>_<value>%` as String.
         */
        override fun getIdentifier(): String? {
            return "moreFish"
        }

        override fun getPlugin(): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        /**
         * This is the version of this expansion.
         * <br></br>You don't have to use numbers, since it is set as a String.
         *
         * @return The version as a String.
         */
        override fun getVersion(): String? {
            return "1.0.0"
        }

        /**
         * This is the method called when a placeholder with our identifier
         * is found and needs a value.
         * <br></br>We specify the value identifier in this method.
         * <br></br>Since version 2.9.1 can you use OfflinePlayers in your requests.
         *
         * @param  player
         * A [Player].
         * @param  identifier
         * A String containing the identifier/value.
         *
         * @return Possibly-null String of the requested identifier.
         */
        private val competition: FishingCompetition = plugin.competition

        override fun onPlaceholderRequest(player: Player?, identifier: String): String? {
            return when {
                identifier.startsWith("top_player_") -> {
                    val number = identifier.replace("top_player_", "").toInt()
                    if (competition.ranking.size >= number)
                        competition.recordOf(number).fisher.name
                    else
                        "没有人"
                }
                identifier.startsWith("top_fish_length_") -> {
                    val number = identifier.replace("top_fish_length_", "").toInt()
                    if (competition.ranking.size >= number)
                        competition.recordOf(number).fish.length.toString()
                    else
                        "0.0"
                }
                identifier.startsWith("top_fish_") -> {
                    val number = identifier.replace("top_fish_", "").toInt()
                    if (competition.ranking.size >= number)
                        competition.recordOf(number).fish.type.name
                    else
                        "无"
                }
                identifier == "rank" -> {
                    require(player != null) { "'rank' placeholder requires a player" }
                    if (competition.containsContestant(player)) {
                        val record = competition.recordOf(player)
                        competition.rankNumberOf(record).toString()
                    } else {
                        "0"
                    }
                }
                identifier == "fish_length" -> {
                    require(player != null) { "'fish_length' placeholder requires a player" }
                    if (competition.containsContestant(player))
                        competition.recordOf(player).fish.length.toString()
                    else
                        "0.0"
                }
                identifier == "fish" -> {
                    require(player != null) { "'fish' placeholder requires a player" }
                    if (competition.containsContestant(player))
                        competition.recordOf(player).fish.type.name
                    else
                        "无"
                }
                else -> null
            }
        }
    }
}