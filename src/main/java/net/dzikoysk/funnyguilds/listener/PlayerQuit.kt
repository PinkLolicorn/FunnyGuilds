package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserCache;
import net.dzikoysk.funnyguilds.element.tablist.AbstractTablist;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        handleQuit(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        handleQuit(event.getPlayer());
    }

    private void handleQuit(Player player) {
        User user = User.get(player);

        if (user != null) {
            UserCache cache = user.getCache();
            cache.setIndividualPrefix(null);
            cache.setScoreboard(null);
            cache.setDummy(null);
            cache.clearDamage();

            user.getBossBar().removeNotification();
        }

        AbstractTablist.removeTablist(player);
    }

}
