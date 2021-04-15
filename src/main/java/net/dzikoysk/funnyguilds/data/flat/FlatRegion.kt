package net.dzikoysk.funnyguilds.data.flat;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.BasicType;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.YamlWrapper;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import org.bukkit.Location;

import java.io.File;

public class FlatRegion {

    private final Region region;

    public FlatRegion(Region region) {
        this.region = region;
    }

    public static Region deserialize(File file) {
        YamlWrapper wrapper = new YamlWrapper(file);
        String name = wrapper.getString("name");
        String centerString = wrapper.getString("center");
        int size = wrapper.getInt("size");
        int enlarge = wrapper.getInt("enlarge");

        if (name == null || centerString == null) {
            FunnyGuilds.getPluginLogger().error("Cannot deserialize region! Caused by: name/center is null");
            return null;
        }

        Location center = LocationUtils.parseLocation(centerString);

        if (center == null) {
            FunnyGuilds.getPluginLogger().error("Cannot deserialize region! Caused by: center is null");
            return null;
        }

        if (size < 1) {
            size = FunnyGuilds.getInstance().getPluginConfiguration().regionSize;
        }

        Object[] values = new Object[4];
        values[0] = name;
        values[1] = center;
        values[2] = size;
        values[3] = enlarge;

        return DeserializationUtils.deserializeRegion(values);
    }

    public boolean serialize(FlatDataModel flatDataModel) {
        File file = flatDataModel.loadCustomFile(BasicType.REGION, region.getName());
        YamlWrapper wrapper = new YamlWrapper(file);

        wrapper.set("name", region.getName());
        wrapper.set("center", LocationUtils.toString(region.getCenter()));
        wrapper.set("size", region.getSize());
        wrapper.set("enlarge", region.getEnlarge());

        wrapper.save();
        return true;
    }

}
