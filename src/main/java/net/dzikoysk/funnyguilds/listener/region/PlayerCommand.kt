package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommand implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("funnyguilds.admin")) {
            return;
        }

        String[] commandElements = event.getMessage().split("\\s+");

        if (commandElements.length == 0) {
            return;
        }

        String command = commandElements[0];

        for (String blockedCommand: FunnyGuilds.getInstance().getPluginConfiguration().regionCommands) {
            if (("/" + blockedCommand).equalsIgnoreCase(command)) {
                command = null;
                break;
            }
        }

        if (command != null) {
            return;
        }

        Region region = RegionUtils.getAt(player.getLocation());

        if (region == null) {
            return;
        }

        Guild guild = region.getGuild();
        User user = User.get(player);

        if (guild.getMembers().contains(user)) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(FunnyGuilds.getInstance().getMessageConfiguration().regionCommand);
    }

}
