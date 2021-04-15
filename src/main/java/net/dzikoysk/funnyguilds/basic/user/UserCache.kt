package net.dzikoysk.funnyguilds.basic.user

import com.google.common.cache.CacheBuilder
import net.dzikoysk.funnyguilds.element.Dummy
import net.dzikoysk.funnyguilds.element.IndividualPrefix
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scoreboard.Scoreboard
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Function

class UserCache(private val user: User) {
    private val damage: MutableMap<User?, Double> = HashMap()
    private val attackerCache = CacheBuilder
        .newBuilder()
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .build<UUID?, Long>()
    private val victimCache = CacheBuilder
        .newBuilder()
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .build<UUID?, Long>()

    @get:Synchronized
    @set:Synchronized
    var scoreboard: Scoreboard? = null
    var individualPrefix: IndividualPrefix? = null
    var dummy: Dummy? = null
        get() {
            if (field == null) {
                field = Dummy(user)
            }
            return field
        }
    var teleportation: BukkitTask? = null
    var notificationTime: Long = 0
    var enter = false

    //private boolean bypass;
    var isSpy = false
    fun addDamage(user: User?, damage: Double) {
        val currentDamage = this.damage[user]
        this.damage[user] = (currentDamage ?: 0.0) + damage
    }

    fun killedBy(user: User?): Double {
        if (user == null) {
            return 0.0
        }
        val dmg = damage.remove(user)
        return dmg ?: 0.0
    }

    fun clearDamage() {
        damage.clear()
    }

    fun registerVictim(user: User?) {
        victimCache.put(user.getUUID(), System.currentTimeMillis())
    }

    fun registerAttacker(user: User?) {
        attackerCache.put(user.getUUID(), System.currentTimeMillis())
    }

    val lastAttacker: User?
        get() {
            val lastAttackerUniqueId = attackerCache.asMap().entries
                .stream()
                .sorted(java.util.Map.Entry.comparingByValue<UUID?, Long>().reversed())
                .map(Function<Entry<UUID?, Long>, UUID?> { obj: Entry<UUID?, Long> -> obj.key }).findFirst()
            return lastAttackerUniqueId.map { obj: UUID? -> UserUtils.get() }.orElse(null)
        }

    fun wasVictimOf(attacker: User?): Long? {
        return attackerCache.getIfPresent(attacker.getUUID())
    }

    fun wasAttackerOf(victim: User?): Long? {
        return victimCache.getIfPresent(victim.getUUID())
    }

    fun getDamage(): Map<User?, Double> {
        return HashMap(damage)
    }

    val totalDamage: Double
        get() {
            var dmg = 0.0
            for (d in damage.values) {
                dmg += d
            }
            return dmg
        }
    val isAssisted: Boolean
        get() = !damage.isEmpty()
}