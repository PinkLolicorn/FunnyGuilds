package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.event.FunnyEvent;
import org.bukkit.Bukkit;

public abstract class GuildEvent extends FunnyEvent {

    private final Guild guild;

    public GuildEvent(EventCause eventCause, User doer, Guild guild) {
        super(eventCause, doer, ! Bukkit.isPrimaryThread());

        this.guild = guild;
    }

    public GuildEvent(EventCause eventCause, User doer, Guild guild, boolean isAsync) {
        super(eventCause, doer, isAsync);

        this.guild = guild;
    }

    public Guild getGuild() {
        return this.guild;
    }
    
}
