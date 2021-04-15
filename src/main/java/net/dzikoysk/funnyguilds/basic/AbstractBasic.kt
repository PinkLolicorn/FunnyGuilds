package net.dzikoysk.funnyguilds.basic

abstract class AbstractBasic : Basic {
    private var wasChanged = true
    override fun markChanged() {
        wasChanged = true
    }

    override fun wasChanged(): Boolean {
        val changedState = wasChanged
        if (changedState) {
            wasChanged = false
        }
        return changedState
    }
}