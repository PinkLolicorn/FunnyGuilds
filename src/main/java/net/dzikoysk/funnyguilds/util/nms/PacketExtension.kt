package net.dzikoysk.funnyguilds.util.nms

import io.netty.channel.*
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager
import net.dzikoysk.funnyguilds.concurrency.requests.WarUseRequest
import org.bukkit.entity.Player
import java.lang.reflect.Field
import java.lang.reflect.Method

object PacketExtension {
    private var clientChannel: Reflections.FieldAccessor<Channel?>? = null
    private var playerConnection: Field? = null
    private var networkManager: Field? = null
    private var handleMethod: Method? = null
    private fun getChannel(player: Player): Channel? {
        return try {
            val eP = handleMethod!!.invoke(player)
            clientChannel!![networkManager!![playerConnection!![eP]]]
        } catch (exception: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("PacketExtension Error", exception)
            null
        }
    }

    fun registerPlayer(player: Player) {
        try {
            val concurrencyManager: ConcurrencyManager = FunnyGuilds.Companion.getInstance().getConcurrencyManager()
            val channel = getChannel(player)
            val handler: ChannelHandler = object : ChannelDuplexHandler() {
                @Throws(Exception::class)
                override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
                    if (msg == null) {
                        return
                    }
                    super.write(ctx, msg, promise)
                }

                @Throws(Exception::class)
                override fun channelRead(ctx: ChannelHandlerContext, packet: Any) {
                    try {
                        if (packet == null) {
                            return
                        }
                        concurrencyManager.postRequests(WarUseRequest(player, packet))
                        super.channelRead(ctx, packet)
                    } catch (e: Exception) {
                        super.channelRead(ctx, packet)
                    }
                }
            }
            if (channel == null) {
                return
            }
            val pipeline = channel.pipeline()
            if (pipeline.names().contains("packet_handler")) {
                if (pipeline.names().contains("FunnyGuilds")) {
                    pipeline.replace("FunnyGuilds", "FunnyGuilds", handler)
                } else {
                    pipeline.addBefore("packet_handler", "FunnyGuilds", handler)
                }
            }
        } catch (exception: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("PacketExtension Error", exception)
        }
    }

    init {
        try {
            clientChannel = Reflections.getField<Channel>(Reflections.getNMSClass("NetworkManager"), Channel::class.java, 0)!!
            playerConnection = Reflections.getField(Reflections.getNMSClass("EntityPlayer"), "playerConnection")
            networkManager = Reflections.getField(Reflections.getNMSClass("PlayerConnection"), "networkManager")
            handleMethod = Reflections.getMethod(Reflections.getCraftBukkitClass("entity.CraftEntity"), "getHandle")
        } catch (exception: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("PacketExtension Error", exception)
        }
    }
}