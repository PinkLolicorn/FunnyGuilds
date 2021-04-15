package net.dzikoysk.funnyguilds.basic.rank;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import org.apache.commons.lang3.StringUtils;

public class RankUtils {

    public static String parseRank(User source, String rankTop) {
        if (! rankTop.contains("TOP-")) {
            return null;
        }

        int i = getIndex(rankTop);

        if (i <= 0) {
            FunnyGuilds.getPluginLogger().error("Index in TOP- must be greater or equal to 1!");
            return null;
        }

        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        if (rankTop.contains("GTOP")) {
            Guild guild = RankManager.getInstance().getGuild(i);

            if (guild == null) {
                return StringUtils.replace(rankTop, "{GTOP-" + i + '}', FunnyGuilds.getInstance().getMessageConfiguration().gtopNoValue);
            }

            int points = guild.getRank().getPoints();
            String pointsFormat = config.gtopPoints;

            if (!pointsFormat.isEmpty()) {
                pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", IntegerRange.inRangeToString(points, config.pointsFormat));
                pointsFormat = pointsFormat.replace("{POINTS}", String.valueOf(points));
            }

            String guildTag = guild.getTag();

            if (config.playerListUseRelationshipColors) {
                guildTag = StringUtils.replace(config.prefixOther, "{TAG}", guild.getTag());

                if (source != null && source.hasGuild()) {
                    Guild sourceGuild = source.getGuild();

                    if (sourceGuild.getAllies().contains(guild)) {
                        guildTag = StringUtils.replace(config.prefixAllies, "{TAG}", guild.getTag());
                    }
                    else if (sourceGuild.getUUID().equals(guild.getUUID())) {
                        guildTag = StringUtils.replace(config.prefixOur, "{TAG}", guild.getTag());
                    }
                }
            }

            return StringUtils.replace(rankTop, "{GTOP-" + i + '}', guildTag + pointsFormat);

        }
        else if (rankTop.contains("PTOP")) {
            User user = RankManager.getInstance().getUser(i);

            if (user == null) {
                return StringUtils.replace(rankTop, "{PTOP-" + i + '}', FunnyGuilds.getInstance().getMessageConfiguration().ptopNoValue);
            }

            int points = user.getRank().getPoints();
            String pointsFormat = config.ptopPoints;

            if (!pointsFormat.isEmpty()) {
                pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", IntegerRange.inRangeToString(points, config.pointsFormat));
                pointsFormat = pointsFormat.replace("{POINTS}", String.valueOf(points));
            }

            return StringUtils.replace(rankTop, "{PTOP-" + i + '}', (user.isOnline() ? config.ptopOnline : config.ptopOffline) + user.getName() + pointsFormat);
        }

        return null;
    }

    public static int getIndex(String rank) {
        StringBuilder sb = new StringBuilder();
        boolean open = false;
        boolean start = false;
        int result = -1;

        for (char c : rank.toCharArray()) {
            boolean end = false;

            switch (c) {
                case '{':
                    open = true;
                    break;
                case '-':
                    start = true;
                    break;
                case '}':
                    end = true;
                    break;
                default:
                    if (open && start) {
                        sb.append(c);
                    }
            }

            if (end) {
                break;
            }
        }

        try {
            result = Integer.parseInt(sb.toString());
        } catch(NumberFormatException e) {
            FunnyGuilds.getPluginLogger().parser(rank + " contains an invalid number: " + sb.toString());
        }

        return result;
    }

    private RankUtils() { }

}
