package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.CanManage;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildExtendValidityEvent;
import net.dzikoysk.funnyguilds.util.commons.TimeUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.List;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

@FunnyComponent
public final class ValidityCommand {

    @FunnyCommand(
        name = "${user.validity.name}",
        description = "${user.validity.description}",
        aliases = "${user.validity.aliases}",
        permission = "funnyguilds.validity",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(PluginConfiguration config, MessageConfiguration messages, Player player, @CanManage User user, Guild guild) {
        if (config.validityWhen != 0) {
            long validity = guild.getValidity();
            long delta = validity - System.currentTimeMillis();

            when (delta > config.validityWhen, messages.validityWhen.replace("{TIME}", TimeUtils.getDurationBreakdown(delta - config.validityWhen)));
        }

        List<ItemStack> requiredItems = config.validityItems;

        if (!ItemUtils.playerHasEnoughItems(player, requiredItems)) {
            return;
        }
        
        if (!SimpleEventHandler.handle(new GuildExtendValidityEvent(EventCause.USER, user, guild, config.validityTime))) {
            return;
        }
        
        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));
        long validity = guild.getValidity();

        if (validity == 0) {
            validity = System.currentTimeMillis();
        }

        validity += config.validityTime;
        guild.setValidity(validity);
        player.sendMessage(messages.validityDone.replace("{DATE}", config.dateFormat.format(new Date(validity))));
    }

}
