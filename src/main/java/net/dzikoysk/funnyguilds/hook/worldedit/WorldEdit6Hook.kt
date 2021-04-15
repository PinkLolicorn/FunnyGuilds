package net.dzikoysk.funnyguilds.hook.worldedit

import com.sk89q.jnbt.NBTInputStream
import com.sk89q.worldedit.MaxChangedBlocksException
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.extent.Extent
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.session.PasteBuilder
import com.sk89q.worldedit.world.World
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.util.nms.Reflections
import org.bukkit.Location
import java.io.*
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.zip.GZIPInputStream

class WorldEdit6Hook : WorldEditHook {
    private var schematicReaderConstructor: Constructor<*>? = null
    private var clipboardHolderConstructor: Constructor<*>? = null
    private var pasteConstructor: Constructor<*>? = null
    private var vectorConstructor: Constructor<*>? = null
    private var getWorldData: Method? = null
    private var pasteBuilderSetTo: Method? = null
    private var readSchematic: Method? = null
    override fun pasteSchematic(schematicFile: File, location: Location, withAir: Boolean): Boolean {
        try {
            val pasteLocation = vectorConstructor!!.newInstance(location.x, location.y, location.z)
            val pasteWorld: World = BukkitWorld(location.world)
            val pasteWorldData = getWorldData!!.invoke(pasteWorld)
            val nbtStream = NBTInputStream(GZIPInputStream(FileInputStream(schematicFile)))
            val reader = schematicReaderConstructor!!.newInstance(nbtStream)
            val clipboard = readSchematic!!.invoke(reader, pasteWorldData)
            val editSession = WorldEdit.getInstance().editSessionFactory.getEditSession(pasteWorld, -1)
            val clipboardHolder = clipboardHolderConstructor!!.newInstance(clipboard, pasteWorldData) as ClipboardHolder
            var builder = pasteConstructor!!.newInstance(clipboardHolder, editSession, pasteWorldData) as PasteBuilder
            builder = pasteBuilderSetTo!!.invoke(builder, pasteLocation) as PasteBuilder
            builder = builder.ignoreAirBlocks(!withAir)
            Operations.completeLegacy(builder.build())
        } catch (ex: InstantiationException) {
            throw RuntimeException("Could not paste schematic: " + schematicFile.absolutePath, ex)
        } catch (ex: IllegalAccessException) {
            throw RuntimeException("Could not paste schematic: " + schematicFile.absolutePath, ex)
        } catch (ex: InvocationTargetException) {
            throw RuntimeException("Could not paste schematic: " + schematicFile.absolutePath, ex)
        } catch (ex: MaxChangedBlocksException) {
            throw RuntimeException("Could not paste schematic: " + schematicFile.absolutePath, ex)
        } catch (ex: IOException) {
            throw RuntimeException("Could not paste schematic: " + schematicFile.absolutePath, ex)
        }
        return true
    }

    override fun init() {
        try {
            val schematicReaderClass = Reflections.getClass("com.sk89q.worldedit.extent.clipboard.io.SchematicReader")
            val worldDataClass = Reflections.getClass("com.sk89q.worldedit.world.registry.WorldData")
            val vectorClass = Reflections.getClass("com.sk89q.worldedit.Vector")
            schematicReaderConstructor = Reflections.getConstructor(
                schematicReaderClass,
                Reflections.getClass("com.sk89q.jnbt.NBTInputStream")
            )
            pasteConstructor = Reflections.getConstructor(PasteBuilder::class.java, ClipboardHolder::class.java, Extent::class.java, worldDataClass)
            clipboardHolderConstructor = Reflections.getConstructor(ClipboardHolder::class.java, Clipboard::class.java, worldDataClass)
            vectorConstructor = Reflections.getConstructor(
                vectorClass,
                Double::class.javaPrimitiveType, Double::class.javaPrimitiveType, Double::class.javaPrimitiveType
            )
            schematicReaderConstructor!!.isAccessible = true
            pasteConstructor!!.isAccessible = true
            clipboardHolderConstructor!!.isAccessible = true
            vectorConstructor!!.isAccessible = true
            getWorldData = World::class.java.getDeclaredMethod("getWorldData")
            readSchematic = schematicReaderClass!!.getDeclaredMethod("read", worldDataClass)
            pasteBuilderSetTo = PasteBuilder::class.java.getDeclaredMethod("to", vectorClass)
            getWorldData.setAccessible(true)
            readSchematic.setAccessible(true)
            pasteBuilderSetTo.setAccessible(true)
        } catch (ex: NoSuchMethodException) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not properly initialize WorldGuard hook!")
        }
    }
}