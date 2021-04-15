package net.dzikoysk.funnyguilds.basic.user

import net.dzikoysk.funnyguilds.util.commons.ChatUtils
import org.panda_lang.utilities.commons.StringUtils

class UserBan(private val reason: String?, val banTime: Long) {
    val isBanned: Boolean
        get() = banTime != 0L

    fun getReason(): String? {
        return if (reason != null) {
            ChatUtils.colored(reason)
        } else StringUtils.EMPTY
    }
}