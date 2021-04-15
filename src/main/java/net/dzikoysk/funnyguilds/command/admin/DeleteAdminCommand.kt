package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.GuildValidation;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.commons.text.Formatter;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

public final class DeleteAdminCommand {

    @FunnyCommand(
        name = "${admin.delete.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(MessageConfiguration messages, CommandSender sender, String[] args) {
        when (args.length < 1, messages.generalNoTagGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        User admin = AdminUtils.getAdminUser(sender);

        if (!SimpleEventHandler.handle(new GuildDeleteEvent(AdminUtils.getCause(admin), admin, guild))) {
            return;
        }
        
        GuildUtils.deleteGuild(guild);

        Formatter formatter = new Formatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{ADMIN}", sender.getName())
                .register("{PLAYER}", sender.getName());

        guild.getOwner().sendMessage(formatter.format(messages.adminGuildBroken));
        sender.sendMessage(formatter.format(messages.deleteSuccessful));
        Bukkit.getServer().broadcastMessage(formatter.format(messages.broadcastDelete));
    }

}
