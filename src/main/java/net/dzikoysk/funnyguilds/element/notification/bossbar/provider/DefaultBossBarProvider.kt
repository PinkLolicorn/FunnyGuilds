package net.dzikoysk.funnyguilds.element.notification.bossbar.provider;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public final class DefaultBossBarProvider implements BossBarProvider {

    private final    User       user;
    private final    BossBar    bossBar;
    private volatile BukkitTask hideBossBarTask;

    public DefaultBossBarProvider(User user) {
        this.user = user;
        this.bossBar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
    }

    @Override
    public void sendNotification(String text, BossBarOptions options, int timeout) {
        Player player = user.getPlayer();

        if (player == null) {
            return;
        }

        this.bossBar.setTitle(text);
        this.bossBar.setColor(options.getColor());
        this.bossBar.setStyle(options.getStyle());
        options.getFlags().forEach(this.bossBar::addFlag);
        this.bossBar.setVisible(true);

        if (! this.bossBar.getPlayers().contains(player)) {
            this.bossBar.addPlayer(player);
        }

        if (this.hideBossBarTask != null) {
            this.hideBossBarTask.cancel();
        }

        this.hideBossBarTask = Bukkit.getScheduler().runTaskLaterAsynchronously(FunnyGuilds.getInstance(), () -> {
            this.bossBar.removePlayer(player);
            this.bossBar.setVisible(false);
        }, 20L * timeout);
    }

    @Override
    public void removeNotification() {
        Player player = user.getPlayer();

        if (player == null) {
            return;
        }

        this.bossBar.removePlayer(player);
        this.bossBar.setVisible(false);
    }

    public BossBar getBossBar() {
        return this.bossBar;
    }

}
