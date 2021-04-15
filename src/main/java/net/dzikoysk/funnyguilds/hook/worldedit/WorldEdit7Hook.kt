package net.dzikoysk.funnyguilds.hook.worldedit

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.WorldEditException
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import net.dzikoysk.funnyguilds.FunnyGuilds
import org.bukkit.Location
import java.io.*

class WorldEdit7Hook : WorldEditHook {
    override fun pasteSchematic(schematicFile: File, location: Location, withAir: Boolean): Boolean {
        return try {
            val pasteLocation = BlockVector3.at(location.x, location.y, location.z)
            val pasteWorld = BukkitAdapter.adapt(location.world)
            val clipboard = ClipboardFormats.findByFile(schematicFile)!!.getReader(FileInputStream(schematicFile)).read()
            val clipboardHolder = ClipboardHolder(clipboard)
            val editSession = WorldEdit.getInstance().editSessionFactory.getEditSession(pasteWorld, -1)
            val operation = clipboardHolder
                .createPaste(editSession)
                .to(pasteLocation)
                .ignoreAirBlocks(!withAir)
                .build()
            Operations.complete(operation)
            editSession.close()
            true
        } catch (e: IOException) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not paste schematic: " + schematicFile.absolutePath, e)
            false
        } catch (e: WorldEditException) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not paste schematic: " + schematicFile.absolutePath, e)
            false
        }
    }

    override fun init() {}
}