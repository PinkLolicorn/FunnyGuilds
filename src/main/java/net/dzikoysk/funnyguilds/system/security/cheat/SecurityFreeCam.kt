package net.dzikoysk.funnyguilds.system.security.cheat

import com.google.common.collect.Streams
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.system.security.SecurityUtils
import net.dzikoysk.funnyguilds.util.commons.bukkit.MaterialUtils
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.BlockIterator
import org.bukkit.util.Vector
import org.panda_lang.utilities.commons.StringUtils
import org.panda_lang.utilities.commons.text.Joiner
import java.util.stream.Collectors

object SecurityFreeCam {
    fun on(player: Player, origin: Vector?, hitPoint: Vector?, distance: Double) {
        val messages: MessageConfiguration = FunnyGuilds.Companion.getInstance().getMessageConfiguration()
        val config: PluginConfiguration = FunnyGuilds.Companion.getInstance().getPluginConfiguration()
        val blockIterator = BlockIterator(player.world, origin!!, hitPoint!!, 0, Math.max(distance.toInt(), 1))
        /* compensationSneaking will be removed after add the cursor height check on each client version. */
        val compensationSneaking = if (player.isSneaking) 1 else 0
        val blocks = Streams.stream(blockIterator)
            .filter { block: Block -> !block.isLiquid }
            .filter { block: Block -> !block.isPassable }
            .filter { block: Block -> block.type.isSolid }
            .filter { block: Block -> block.type.isOccluding || block.type == Material.GLASS }
            .limit(8)
            .collect(Collectors.toList())
        if (blocks.size <= config.freeCamCompensation + compensationSneaking) {
            return
        }
        var message = messages.securitySystemFreeCam
        message = StringUtils.replace(message, "{BLOCKS}, ", Joiner.on(", ").join(blocks) { b: Block -> MaterialUtils.getMaterialName(b.type) }
            .toString())
        SecurityUtils.addViolationLevel(User.Companion.get(player))
        SecurityUtils.sendToOperator(player, "FreeCam", message)
    }
}