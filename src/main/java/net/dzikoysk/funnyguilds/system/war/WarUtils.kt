package net.dzikoysk.funnyguilds.system.war;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.util.commons.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

public final class WarUtils {

    private WarUtils() {}

    public static void message(Player player, int i, Object... values) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        String message = null;
        
        switch (i) {
            case 0:
                message = messages.warHasNotGuild;
                break;
            case 1:
                message = messages.warAlly;
                break;
            case 2:
                message = messages.warWait;
                message = StringUtils.replace(message, "{TIME}", TimeUtils.getDurationBreakdown((long) values[0]));
                break;
            case 3:
                message = messages.warAttacker;
                message = StringUtils.replace(message, "{ATTACKED}", ((Guild) values[0]).getTag());
                break;
            case 4: 
                message = messages.warAttacked;
                message = StringUtils.replace(message, "{ATTACKER}", ((Guild) values[0]).getTag());
                break;
            case 5:
                message = messages.warDisabled;
                break;
        }
        
        player.sendMessage(message);
    }

    public static String getWinMessage(Guild conqueror, Guild loser) {
        return FunnyGuilds.getInstance().getMessageConfiguration().warWin
                .replace("{WINNER}", conqueror.getTag())
                .replace("{LOSER}", loser.getTag());
    }

    public static String getLoseMessage(Guild conqueror, Guild loser) {
        return FunnyGuilds.getInstance().getMessageConfiguration().warLose
                .replace("{WINNER}", conqueror.getTag())
                .replace("{LOSER}", loser.getTag());
    }

    public static String getBroadcastMessage(Guild conqueror, Guild loser) {
        return FunnyGuilds.getInstance().getMessageConfiguration().broadcastWar
                .replace("{WINNER}", conqueror.getTag())
                .replace("{LOSER}", loser.getTag());
    }

}
