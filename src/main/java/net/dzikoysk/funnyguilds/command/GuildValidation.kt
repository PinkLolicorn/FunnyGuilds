package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import org.apache.commons.lang3.StringUtils;

public final class GuildValidation {

    private GuildValidation() {}

    public static Guild requireGuildByTag(String tag) {
        Guild guild = GuildUtils.getByTag(tag);

        if (guild == null) {
           throw new ValidationException(StringUtils.replace(FunnyGuilds.getInstance().getMessageConfiguration().generalGuildNotExists, "{TAG}", tag));
        }

        return guild;
    }

}
