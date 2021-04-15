package net.dzikoysk.funnyguilds.basic.user

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.Guild
import org.apache.commons.lang3.Validate
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern
import java.util.stream.Collectors

object UserUtils {
    private val USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,16}$")
    private val BY_UUID_USER_COLLECTION: MutableMap<UUID?, User> = ConcurrentHashMap()
    private val BY_NAME_USER_COLLECTION: MutableMap<String?, User> = ConcurrentHashMap()
    val users: Set<User>
        get() = HashSet(BY_UUID_USER_COLLECTION.values)

    @JvmOverloads
    operator fun get(nickname: String?, ignoreCase: Boolean = false): User? {
        return if (ignoreCase) {
            for ((key, value) in BY_NAME_USER_COLLECTION) {
                if (key.equals(nickname, ignoreCase = true)) {
                    return value
                }
            }
            null
        } else {
            BY_NAME_USER_COLLECTION[nickname]
        }
    }

    operator fun get(uuid: UUID?): User? {
        return BY_UUID_USER_COLLECTION[uuid]
    }

    fun addUser(user: User) {
        Validate.notNull(user, "user can't be null!")
        BY_UUID_USER_COLLECTION[user.uuid] = user
        BY_NAME_USER_COLLECTION[user.name] = user
    }

    fun removeUser(user: User) {
        Validate.notNull(user, "user can't be null!")
        BY_UUID_USER_COLLECTION.remove(user.uuid)
        BY_NAME_USER_COLLECTION.remove(user.name)
    }

    fun updateUsername(user: User, newUsername: String) {
        Validate.notNull(user, "user can't be null!")
        BY_NAME_USER_COLLECTION.remove(user.name)
        BY_NAME_USER_COLLECTION[newUsername] = user
        user.name = newUsername
    }

    @JvmOverloads
    fun playedBefore(nickname: String?, ignoreCase: Boolean = false): Boolean {
        return if (ignoreCase) {
            if (nickname != null) {
                for (userNickname in BY_NAME_USER_COLLECTION.keys) {
                    if (userNickname.equals(nickname, ignoreCase = true)) {
                        return true
                    }
                }
            }
            false
        } else {
            nickname != null && BY_NAME_USER_COLLECTION.containsKey(nickname)
        }
    }

    fun getNames(users: Collection<User?>?): Set<String?> {
        return users!!.stream().map { obj: User? -> obj!!.name }.collect(Collectors.toSet())
    }

    fun getUsers(names: Collection<String?>?): Set<User> {
        val users: MutableSet<User> = HashSet()
        for (name in names!!) {
            val user: User = User.Companion.get(name)
            if (user == null) {
                FunnyGuilds.Companion.getPluginLogger().warning("Corrupted user: $name")
                continue
            }
            users.add(user)
        }
        return users
    }

    fun getOnlineNames(users: Collection<User?>?): Set<String> {
        val set: MutableSet<String> = HashSet()
        for (user in users!!) {
            set.add((if (user!!.isOnline) "<online>" + user.name + "</online>" else user.name)!!)
        }
        return set
    }

    fun removeGuild(users: Collection<User?>?) {
        for (user in users!!) {
            user!!.removeGuild()
        }
    }

    fun setGuild(users: Collection<User?>, guild: Guild?) {
        for (user in users) {
            user!!.guild = guild
        }
    }

    fun usersSize(): Int {
        return BY_UUID_USER_COLLECTION.size
    }

    fun validateUsername(name: String?): Boolean {
        return USERNAME_PATTERN.matcher(name).matches()
    }
}