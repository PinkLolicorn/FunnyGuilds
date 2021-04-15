package net.dzikoysk.funnyguilds.util.commons.bukkit

import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.panda_lang.utilities.commons.function.Option

object EntityUtils {
    fun getAttacker(damager: Entity?): Option<Player?> {
        if (damager is Player) {
            return Option.of(damager as Player?)
        }
        if (damager is Projectile) {
            val shooter = damager.shooter
            if (shooter is Player) {
                return Option.of(shooter as Player?)
            }
        }
        return Option.none()
    }
}