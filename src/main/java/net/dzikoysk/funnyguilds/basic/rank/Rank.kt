package net.dzikoysk.funnyguilds.basic.rank

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.Basic
import net.dzikoysk.funnyguilds.basic.BasicType
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.user.User

class Rank(val basic: Basic) : Comparable<Rank> {
    val type: BasicType?
    val identityName: String?
    var guild: Guild? = null
    var user: User? = null
    var position = 0
    private var points = 0
    private var kills = 0
    private var deaths = 0
    fun removePoints(value: Int) {
        points -= value
        if (points < 1) {
            points = 0
        }
        basic.markChanged()
    }

    fun addPoints(value: Int) {
        points += value
        basic.markChanged()
    }

    fun addKill() {
        kills++
        basic.markChanged()
    }

    fun addDeath() {
        deaths++
        basic.markChanged()
    }

    override fun compareTo(rank: Rank): Int {
        var result = Integer.compare(getPoints(), rank.getPoints())
        if (result == 0) {
            if (identityName == null) {
                return -1
            }
            if (rank.identityName == null) {
                return 1
            }
            result = identityName.compareTo(rank.identityName)
        }
        return result
    }

    fun getPoints(): Int {
        if (type == BasicType.USER) {
            return points
        }
        var points = 0.0
        val size = guild!!.members.size
        if (size == 0) {
            return 0
        }
        for (user in guild.members) {
            points += user.rank.points.toDouble()
        }
        val calc = points / size
        if (calc != this.points.toDouble()) {
            this.points = calc.toInt()
            basic.markChanged()
        }
        return this.points
    }

    fun setPoints(points: Int) {
        this.points = points
        basic.markChanged()
    }

    fun getKills(): Int {
        if (type == BasicType.USER) {
            return kills
        }
        var kills = 0
        for (user in guild!!.members) {
            kills += user.rank.kills
        }
        return kills
    }

    fun setKills(kills: Int) {
        this.kills = kills
        basic.markChanged()
    }

    fun getDeaths(): Int {
        if (type == BasicType.USER) {
            return deaths
        }
        var deaths = 0
        for (user in guild!!.members) {
            deaths += user.rank.deaths
        }
        return deaths
    }

    fun setDeaths(deaths: Int) {
        this.deaths = deaths
        basic.markChanged()
    }

    val kDR: Float
        get() = if (getDeaths() == 0) {
            getKills().toFloat()
        } else 1.0f * getKills() / getDeaths()

    override fun equals(o: Any?): Boolean {
        if (o == null) {
            return false
        }
        if (o.javaClass != this.javaClass) {
            return false
        }
        val rank = o as Rank
        return if (rank.type != type) {
            false
        } else rank.identityName == identityName
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + identityName.hashCode()
        return result
    }

    override fun toString(): String {
        return Integer.toString(getPoints())
    }

    init {
        type = basic.type
        identityName = basic.name
        if (type == BasicType.GUILD) {
            guild = basic as Guild
        } else if (type == BasicType.USER) {
            user = basic as User
            points = FunnyGuilds.Companion.getInstance().getPluginConfiguration().rankStart
        }
    }
}