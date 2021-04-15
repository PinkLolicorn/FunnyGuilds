package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;

public class GuildBaseChangeEvent extends GuildEvent {

    private final Location newBaseLocation;
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public GuildBaseChangeEvent(EventCause eventCause, User doer, Guild guild, Location newBaseLocation) {
        super(eventCause, doer, guild);
        
        this.newBaseLocation = newBaseLocation;
    }

    public Location getNewBaseLocation() {
        return this.newBaseLocation;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild base location change has been cancelled by the server!";
    }
    
}