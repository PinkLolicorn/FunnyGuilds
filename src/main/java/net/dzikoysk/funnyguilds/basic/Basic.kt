package net.dzikoysk.funnyguilds.basic

interface Basic {
    fun markChanged()
    fun wasChanged(): Boolean
    val type: BasicType?
    val name: String?
}