package net.dzikoysk.funnyguilds.data.flat;

import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.YamlWrapper;

import java.io.File;

public class FlatUser {

    private final User user;

    public FlatUser(User user) {
        this.user = user;
    }

    public static User deserialize(File file) {
        if (file.isDirectory()) {
            return null;
        }

        YamlWrapper wrapper = new YamlWrapper(file);

        String id = wrapper.getString("uuid");
        String name = wrapper.getString("name");
        int points = wrapper.getInt("points");
        int kills = wrapper.getInt("kills");
        int deaths = wrapper.getInt("deaths");
        long ban = wrapper.getLong("ban");
        String reason = wrapper.getString("reason");

        if (id == null || name == null) {
            return null;
        }

        Object[] values = new Object[7];
        values[0] = id;
        values[1] = name;
        values[2] = points;
        values[3] = kills;
        values[4] = deaths;
        values[5] = ban;
        values[6] = reason;
        
        return DeserializationUtils.deserializeUser(values);
    }

    public boolean serialize(FlatDataModel flatDataModel) {
        File file = flatDataModel.getUserFile(user);
        if (file.isDirectory()) {
            return false;
        }

        YamlWrapper wrapper = new YamlWrapper(file);
        
        wrapper.set("uuid", user.getUUID().toString());
        wrapper.set("name", user.getName());
        wrapper.set("points", user.getRank().getPoints());
        wrapper.set("kills", user.getRank().getKills());
        wrapper.set("deaths", user.getRank().getDeaths());

        if (user.isBanned()) {
            wrapper.set("ban", user.getBan().getBanTime());
            wrapper.set("reason", user.getBan().getReason());
        }

        wrapper.save();
        return true;
    }
}
