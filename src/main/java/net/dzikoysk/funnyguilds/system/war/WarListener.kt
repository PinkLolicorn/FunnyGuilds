package net.dzikoysk.funnyguilds.system.war

import net.dzikoysk.funnycommands.resources.ValidationException
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.command.user.InfoCommand
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.system.security.SecuritySystem
import net.dzikoysk.funnyguilds.util.nms.GuildEntityHelper
import net.dzikoysk.funnyguilds.util.nms.Reflections
import org.bukkit.entity.Player
import java.lang.reflect.Field
import java.util.concurrent.TimeUnit

object WarListener {
    private val INFO_EXECUTOR = InfoCommand()
    private val USE_ENTITY_CLASS: Class<*>? = null
    private val PACKET_ID_FIELD: Field? = null
    private val PACKET_ACTION_FIELD: Field? = null
    private val ENUM_HAND_FIELD: Field? = null
    fun use(player: Player, packet: Any?) {
        try {
            if (packet == null) {
                return
            }
            if (packet.javaClass != USE_ENTITY_CLASS) {
                return
            }
            if (PACKET_ACTION_FIELD == null) {
                return
            }
            val id = PACKET_ID_FIELD!!.getInt(packet)
            val actionEnum = PACKET_ACTION_FIELD[packet]
            var enumHand: Any? = ""
            if (ENUM_HAND_FIELD != null) {
                enumHand = ENUM_HAND_FIELD[packet]
            }
            if (actionEnum == null) {
                return
            }
            call(player, id, actionEnum.toString(), enumHand?.toString() ?: "")
        } catch (exception: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("WarListener error", exception)
        }
    }

    private fun call(player: Player, id: Int, action: String, hand: String) {
        for ((guild, value) in GuildEntityHelper.getGuildEntities()) {
            if (value != id) {
                continue
            }
            if (SecuritySystem.onHitCrystal(player, guild)) {
                return
            }
            if ("ATTACK".equals(action, ignoreCase = true)) {
                WarSystem.Companion.getInstance()!!.attack(player, guild)
                return
            }
            if ("INTERACT_AT".equals(action, ignoreCase = true)) {
                val config: PluginConfiguration = FunnyGuilds.Companion.getInstance().getPluginConfiguration()
                if (config.informationMessageCooldowns.cooldown(player, TimeUnit.SECONDS, config.infoPlayerCooldown.toLong())) {
                    return
                }
                if (!hand.isEmpty() && !"MAIN_HAND".equals(hand, ignoreCase = true)) {
                    return
                }
                try {
                    INFO_EXECUTOR.execute(config, FunnyGuilds.Companion.getInstance().getMessageConfiguration(), player, arrayOf(guild.tag))
                } catch (validatorException: ValidationException) {
                    validatorException.validationMessage.peek { s: String? -> player.sendMessage(s!!) }
                }
            }
        }
    }

    init {
        USE_ENTITY_CLASS = Reflections.getNMSClass("PacketPlayInUseEntity")
        PACKET_ID_FIELD = Reflections.getPrivateField(USE_ENTITY_CLASS, "a")
        PACKET_ACTION_FIELD = Reflections.getPrivateField(USE_ENTITY_CLASS, "action")
        ENUM_HAND_FIELD = if (Reflections.SERVER_VERSION.startsWith("v1_8")) null else Reflections.getPrivateField(USE_ENTITY_CLASS, "d")
    }
}