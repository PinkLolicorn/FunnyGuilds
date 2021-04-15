package net.dzikoysk.funnyguilds.data.flat

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.BasicType
import net.dzikoysk.funnyguilds.basic.guild.*
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils
import net.dzikoysk.funnyguilds.util.YamlWrapper
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils
import java.io.*

class FlatRegion(private val region: Region?) {
    fun serialize(flatDataModel: FlatDataModel): Boolean {
        val file = flatDataModel.loadCustomFile(BasicType.REGION, region!!.name)
        val wrapper = YamlWrapper(file)
        wrapper["name"] = region.name
        wrapper["center"] = LocationUtils.toString(region.center)
        wrapper["size"] = region.size
        wrapper["enlarge"] = region.enlarge
        wrapper.save()
        return true
    }

    companion object {
        fun deserialize(file: File?): Region? {
            val wrapper = YamlWrapper(file)
            val name = wrapper.getString("name")
            val centerString = wrapper.getString("center")
            var size = wrapper.getInt("size")
            val enlarge = wrapper.getInt("enlarge")
            if (name == null || centerString == null) {
                FunnyGuilds.Companion.getPluginLogger().error("Cannot deserialize region! Caused by: name/center is null")
                return null
            }
            val center = LocationUtils.parseLocation(centerString)
            if (center == null) {
                FunnyGuilds.Companion.getPluginLogger().error("Cannot deserialize region! Caused by: center is null")
                return null
            }
            if (size < 1) {
                size = FunnyGuilds.Companion.getInstance().getPluginConfiguration().regionSize
            }
            val values = arrayOfNulls<Any>(4)
            values[0] = name
            values[1] = center
            values[2] = size
            values[3] = enlarge
            return DeserializationUtils.deserializeRegion(values)
        }
    }
}