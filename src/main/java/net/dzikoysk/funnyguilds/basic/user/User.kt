package net.dzikoysk.funnyguilds.basic.user

import com.google.common.base.Charsets
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.AbstractBasic
import net.dzikoysk.funnyguilds.basic.BasicType
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.rank.Rank
import net.dzikoysk.funnyguilds.basic.rank.RankManager
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager
import net.dzikoysk.funnyguilds.concurrency.requests.rank.RankUpdateUserRequest
import net.dzikoysk.funnyguilds.element.notification.bossbar.provider.BossBarProvider
import net.dzikoysk.funnyguilds.util.commons.bukkit.PingUtils
import org.apache.commons.lang3.Validate
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.lang.ref.WeakReference
import java.util.*

class User private constructor(uuid: UUID, name: String) : AbstractBasic() {
    val uUID: UUID?
    private override var name: String
    val cache: UserCache
    val rank: Rank
    private var playerRef: WeakReference<Player?>
    private var guild: Guild? = null
    var ban: UserBan? = null
    val bossBar: BossBarProvider

    private constructor(name: String) : this(UUID.nameUUIDFromBytes("OfflinePlayer:$name".toByteArray(Charsets.UTF_8)), name) {}
    private constructor(player: Player?) : this(player!!.uniqueId, player.name) {}

    fun removeGuild() {
        guild = null
        markChanged()
        val concurrencyManager: ConcurrencyManager = FunnyGuilds.Companion.getInstance().getConcurrencyManager()
        concurrencyManager.postRequests(RankUpdateUserRequest(this))
    }

    fun hasGuild(): Boolean {
        return guild != null
    }

    fun setGuild(guild: Guild?) {
        this.guild = guild
        markChanged()
    }

    fun canManage(): Boolean {
        return isOwner || isDeputy
    }

    val isOwner: Boolean
        get() = if (!hasGuild()) {
            false
        } else guild!!.owner == this
    val isDeputy: Boolean
        get() = if (!hasGuild()) {
            false
        } else guild!!.deputies.contains(this)
    val isOnline: Boolean
        get() = if (name == null) {
            false
        } else Bukkit.getPlayer(uUID!!) != null
    val isBanned: Boolean
        get() = ban != null && ban!!.isBanned

    fun getGuild(): Guild? {
        return guild
    }

    val player: Player?
        get() {
            if (!isOnline) {
                return null
            }
            var player = playerRef.get()
            if (player != null) {
                return player
            }
            player = Bukkit.getPlayer(uUID!!)
            if (player != null) {
                playerRef = WeakReference(player)
                return player
            }
            return null
        }
    val offlinePlayer: OfflinePlayer
        get() = Bukkit.getOfflinePlayer(uUID!!)
    val ping: Int
        get() = PingUtils.getPing(player)

    override fun getName(): String? {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    override val type: BasicType?
        get() = BasicType.USER

    fun updateReference(player: Player) {
        Validate.notNull(player, "you can't update reference with null player!")
        playerRef = WeakReference(player)
    }

    fun sendMessage(message: String?): Boolean {
        val player = player ?: return false
        player.sendMessage(message!!)
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (name == null) 0 else name.hashCode()
        result = prime * result + if (uUID == null) 0 else uUID.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null) {
            return false
        }
        if (obj.javaClass != this.javaClass) {
            return false
        }
        val user = obj as User
        return if (user.uUID != uUID) {
            false
        } else user.getName() == name
    }

    override fun toString(): String {
        return name
    }

    companion object {
        fun create(uuid: UUID, name: String): User {
            Validate.notNull(uuid, "uuid can't be null!")
            Validate.notNull(name, "name can't be null!")
            Validate.notBlank(name, "name can't be blank!")
            Validate.isTrue(UserUtils.validateUsername(name), "name is not valid!")
            val user = User(uuid, name)
            UserUtils.addUser(user)
            RankManager.Companion.getInstance().update(user)
            return user
        }

        fun create(player: Player): User {
            Validate.notNull(player, "player can't be null!")
            val user = User(player)
            UserUtils.addUser(user)
            RankManager.Companion.getInstance().update(user)
            return user
        }

        operator fun get(uuid: UUID?): User? {
            return UserUtils.get(uuid)
        }

        operator fun get(player: Player?): User? {
            return if (player!!.uniqueId.version() == 2) {
                User(player)
            } else UserUtils.get(player.uniqueId)
        }

        operator fun get(offline: OfflinePlayer): User {
            return get(offline.name)
        }

        operator fun get(name: String?): User {
            return get(name)
        }
    }

    init {
        uUID = uuid
        this.name = name
        cache = UserCache(this)
        rank = Rank(this)
        playerRef = WeakReference(Bukkit.getPlayer(uUID))
        bossBar = BossBarProvider.Companion.getBossBar(this)
        markChanged()
    }
}