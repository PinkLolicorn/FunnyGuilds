package net.dzikoysk.funnyguilds.util

import java.util.concurrent.TimeUnit

java.util.*import java.util.concurrent.TimeUnit

class Cooldown<T> {
    private val cooldowns: MutableMap<T, Long> = WeakHashMap(32)
    fun isOnCooldown(key: T): Boolean {
        val cooldown = cooldowns[key] ?: return false
        if (cooldown > System.currentTimeMillis()) {
            return true
        }
        cooldowns.remove(key)
        return false
    }

    fun putOnCooldown(key: T, unit: TimeUnit, duration: Long) {
        cooldowns[key] = System.currentTimeMillis() + unit.toMillis(duration)
    }

    fun putOnCooldown(key: T, cooldown: Long) {
        cooldowns[key] = System.currentTimeMillis() + cooldown
    }

    fun cooldown(key: T, unit: TimeUnit, duration: Long): Boolean {
        return this.cooldown(key, unit.toMillis(duration))
    }

    fun cooldown(key: T, cooldown: Long): Boolean {
        if (isOnCooldown(key)) {
            return true
        }
        this.putOnCooldown(key, cooldown)
        return false
    }
}