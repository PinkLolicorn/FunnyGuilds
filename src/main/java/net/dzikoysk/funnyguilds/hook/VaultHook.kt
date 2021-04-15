package net.dzikoysk.funnyguilds.hook

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import net.milkbowl.vault.permission.Permission
import org.apache.commons.lang3.Validate
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

object VaultHook {
    private var economyHook: Economy? = null
    private var permissionHook: Permission? = null
    fun initHooks() {
        val economyProvider = Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)
        val permissionProvider = Bukkit.getServer().servicesManager.getRegistration(
            Permission::class.java
        )
        if (economyProvider != null) {
            economyHook = economyProvider.provider
        } else {
            FunnyGuilds.Companion.getPluginLogger().warning("No economy provider found, some features may not be available")
        }
        if (permissionProvider != null) {
            permissionHook = permissionProvider.provider
        } else {
            FunnyGuilds.Companion.getPluginLogger().warning("No permission provider found, some features may not be available")
        }
    }

    val isEconomyHooked: Boolean
        get() = economyHook != null
    val isPermissionHooked: Boolean
        get() = permissionHook != null

    fun accountBalance(player: Player): Double {
        Validate.notNull(player, "Player can not be null!")
        return economyHook!!.getBalance(player)
    }

    fun canAfford(player: Player, money: Double): Boolean {
        Validate.notNull(player, "Player can not be null!")
        return economyHook!!.has(player, money)
    }

    fun withdrawFromPlayerBank(player: Player, money: Double): EconomyResponse {
        Validate.notNull(player, "Player can not be null!")
        return economyHook!!.withdrawPlayer(player, money)
    }

    fun hasPermission(player: OfflinePlayer?, permission: String?): Boolean {
        return permissionHook!!.playerHas(null, player, permission)
    }
}