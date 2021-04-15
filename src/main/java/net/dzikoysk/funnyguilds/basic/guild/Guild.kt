package net.dzikoysk.funnyguilds.basic.guild

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.AbstractBasic
import net.dzikoysk.funnyguilds.basic.BasicType
import net.dzikoysk.funnyguilds.basic.rank.Rank
import net.dzikoysk.funnyguilds.basic.rank.RankManager
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.basic.user.UserUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

class Guild private constructor(uuid: UUID) : AbstractBasic() {
    val uUID: UUID?
    private override var name: String? = null
    var tag: String? = null
        set(tag) {
            field = tag
            markChanged()
        }
    private var owner: User? = null
    var rank: Rank? = null
        get() {
            if (field != null) {
                return field
            }
            field = Rank(this)
            RankManager.Companion.getInstance().update(this)
            return field
        }
        set(rank) {
            field = rank
            markChanged()
        }
    var region: Region? = null
        private set
    private var home: Location? = null
    private var members: MutableSet<User?>
    private var deputies: MutableSet<User?>
    private var allies: MutableSet<Guild?>?
    private var enemies: MutableSet<Guild?>?
    var enderCrystal: Location? = null
    private var pvp = false
    private var born: Long
    private var validity: Long = 0
    private var validityDate: Date? = null
    private var attacked: Long = 0
    private var ban: Long = 0
    private var lives = 0
    private var build: Long = 0
    var additionalProtectionEndTime: Long = 0
        private set
    private val alliedFFGuilds: MutableSet<UUID?>

    constructor(name: String?) : this(UUID.randomUUID()) {
        this.name = name
    }

    fun broadcast(message: String?) {
        for (user in onlineMembers) {
            if (user.player == null) {
                continue
            }
            user.player.sendMessage(message)
        }
    }

    fun addLive() {
        lives++
        markChanged()
    }

    fun addMember(user: User?) {
        if (members.contains(user)) {
            return
        }
        members.add(user)
        updateRank()
        markChanged()
    }

    fun addAlly(guild: Guild?) {
        markChanged()
        if (allies!!.contains(guild)) {
            return
        }
        allies!!.add(guild)
    }

    fun addEnemy(guild: Guild?) {
        if (enemies!!.contains(guild)) {
            return
        }
        enemies!!.add(guild)
        markChanged()
    }

    fun deserializationUpdate() {
        owner!!.guild = this
        UserUtils.setGuild(members, this)
    }

    fun removeLive() {
        lives--
        markChanged()
    }

    fun removeMember(user: User?) {
        deputies.remove(user)
        members.remove(user)
        updateRank()
        markChanged()
    }

    fun removeAlly(guild: Guild?) {
        allies!!.remove(guild)
        markChanged()
    }

    fun removeEnemy(guild: Guild?) {
        enemies!!.remove(guild)
        markChanged()
    }

    fun delete() {
        GuildUtils.removeGuild(this)
    }

    fun canBuild(): Boolean {
        if (build > System.currentTimeMillis()) {
            return false
        }
        build = 0
        markChanged()
        return true
    }

    fun updateRank() {
        rank
        RankManager.Companion.getInstance().update(this)
    }

    fun canBeAttacked(): Boolean {
        return protectionEndTime < System.currentTimeMillis() && additionalProtectionEndTime < System.currentTimeMillis()
    }

    fun addDeputy(user: User?) {
        if (deputies.contains(user)) {
            return
        }
        deputies.add(user)
        markChanged()
    }

    fun removeDeputy(user: User?) {
        deputies.remove(user)
        markChanged()
    }

    fun setAdditionalProtection(timestamp: Long) {
        additionalProtectionEndTime = timestamp
    }

    fun setName(name: String?) {
        this.name = name
        markChanged()
    }

    fun setOwner(user: User?) {
        owner = user
        addMember(user)
        markChanged()
    }

    fun setDeputies(users: MutableSet<User?>) {
        deputies = users
        markChanged()
    }

    fun setRegion(region: Region) {
        if (!FunnyGuilds.Companion.getInstance().getPluginConfiguration().regionsEnabled) {
            return
        }
        this.region = region
        this.region!!.guild = this
        if (home == null) {
            home = region.center
        }
        markChanged()
    }

    fun setHome(home: Location?) {
        this.home = home
        markChanged()
    }

    fun setMembers(members: Set<User?>?) {
        this.members = Collections.synchronizedSet(members)
        updateRank()
        markChanged()
    }

    fun setAllies(guilds: MutableSet<Guild?>?) {
        allies = guilds
        markChanged()
    }

    fun setEnemies(guilds: MutableSet<Guild?>?) {
        enemies = guilds
        markChanged()
    }

    fun setPvP(alliedGuild: Guild, enablePvp: Boolean) {
        if (enablePvp) {
            alliedFFGuilds.add(alliedGuild.uUID)
        } else {
            alliedFFGuilds.remove(alliedGuild.uUID)
        }
    }

    fun getPvP(alliedGuild: Guild?): Boolean {
        return allies!!.contains(alliedGuild) && alliedFFGuilds.contains(alliedGuild!!.uUID)
    }

    fun setBorn(l: Long) {
        born = l
        markChanged()
    }

    fun setValidity(l: Long) {
        if (l == born) {
            validity = System.currentTimeMillis() + FunnyGuilds.Companion.getInstance().getPluginConfiguration().validityStart
        } else {
            validity = l
        }
        validityDate = Date(validity)
        markChanged()
    }

    fun setAttacked(l: Long) {
        attacked = l
        markChanged()
    }

    fun setLives(i: Int) {
        lives = i
        markChanged()
    }

    fun setBan(l: Long) {
        if (l > System.currentTimeMillis()) {
            ban = l
        } else {
            ban = 0
        }
        markChanged()
    }

    fun setBuild(time: Long) {
        build = time
        markChanged()
    }

    val protectionEndTime: Long
        get() = if (attacked == born) attacked + FunnyGuilds.Companion.getInstance().getPluginConfiguration().warProtection else attacked + FunnyGuilds.Companion.getInstance()
            .getPluginConfiguration().warWait
    val isSomeoneInRegion: Boolean
        get() = FunnyGuilds.Companion.getInstance().getPluginConfiguration().regionsEnabled && Bukkit.getOnlinePlayers().stream()
            .filter { player: Player? -> User.Companion.get(player)!!.getGuild() !== this }
            .map { player: Player -> RegionUtils.getAt(player.location) }
            .anyMatch { region: Region? -> region != null && region.guild === this }
    val isValid: Boolean
        get() {
            if (validity == born || validity == 0L) {
                validity = System.currentTimeMillis() + FunnyGuilds.Companion.getInstance().getPluginConfiguration().validityStart
                markChanged()
            }
            return validity >= System.currentTimeMillis()
        }
    val isBanned: Boolean
        get() = ban > System.currentTimeMillis()

    override fun getName(): String? {
        return name
    }

    fun getOwner(): User? {
        return owner
    }

    fun getDeputies(): Set<User?> {
        return deputies
    }

    fun getHome(): Location? {
        return home
    }

    fun getMembers(): Set<User?> {
        return members
    }

    val onlineMembers: Set<User>
        get() = members
            .stream()
            .filter { obj: User? -> obj!!.isOnline }
            .collect(Collectors.toSet())

    fun getAllies(): Set<Guild?>? {
        return allies
    }

    fun getEnemies(): Set<Guild?>? {
        return enemies
    }

    var pvP: Boolean
        get() = pvp
        set(b) {
            pvp = b
            markChanged()
        }

    fun getBorn(): Long {
        return born
    }

    fun getValidity(): Long {
        return validity
    }

    fun getValidityDate(): Date {
        return if (validityDate == null) Date(validity).also { validityDate = it } else validityDate!!
    }

    fun getAttacked(): Long {
        return attacked
    }

    fun getLives(): Int {
        return lives
    }

    fun getBan(): Long {
        return ban
    }

    fun getBuild(): Long {
        return build
    }

    override val type: BasicType?
        get() = BasicType.GUILD

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (uUID == null) 0 else uUID.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (obj === this) {
            return true
        }
        return if (obj == null || obj.javaClass != this.javaClass) {
            false
        } else (obj as Guild).uUID == uUID
    }

    override fun toString(): String {
        return name!!
    }

    companion object {
        fun getOrCreate(uuid: UUID): Guild? {
            for (guild in GuildUtils.getGuilds()) {
                if (guild!!.uUID == uuid) {
                    return guild
                }
            }
            val newGuild = Guild(uuid)
            GuildUtils.addGuild(newGuild)
            return newGuild
        }

        fun getOrCreate(name: String?): Guild? {
            for (guild in GuildUtils.getGuilds()) {
                if (guild!!.name.equals(name, ignoreCase = true)) {
                    return guild
                }
            }
            val newGuild = Guild(name)
            GuildUtils.addGuild(newGuild)
            return newGuild
        }
    }

    init {
        uUID = uuid
        born = System.currentTimeMillis()
        members = ConcurrentHashMap.newKeySet()
        deputies = ConcurrentHashMap.newKeySet()
        allies = ConcurrentHashMap.newKeySet()
        enemies = ConcurrentHashMap.newKeySet()
        alliedFFGuilds = ConcurrentHashMap.newKeySet()
    }
}