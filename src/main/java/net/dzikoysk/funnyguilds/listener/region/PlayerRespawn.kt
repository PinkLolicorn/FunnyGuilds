package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.nms.GuildEntityHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawn implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(final PlayerRespawnEvent event) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        Player player = event.getPlayer();
        User user = User.get(player);

        if (! user.hasGuild()) {
            return;
        }

        Location home = user.getGuild().getHome();

        if (home == null) {
            return;
        }

        event.setRespawnLocation(home);

        if (config.createEntityType == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(FunnyGuilds.getInstance(),  () -> {
            Region guildRegion = RegionUtils.getAt(home);

            if (guildRegion == null) {
                return;
            }

            Guild guild = guildRegion.getGuild();
            GuildEntityHelper.spawnGuildHeart(guild, player);
        });
    }
}
