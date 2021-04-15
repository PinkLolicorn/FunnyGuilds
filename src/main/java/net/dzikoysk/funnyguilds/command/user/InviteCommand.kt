package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.CanManage;
import net.dzikoysk.funnyguilds.command.UserValidation;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberInviteEvent;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberRevokeInviteEvent;
import org.bukkit.entity.Player;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

@FunnyComponent
public final class InviteCommand {

    @FunnyCommand(
        name = "${user.invite.name}",
        description = "${user.invite.description}",
        aliases = "${user.invite.aliases}",
        permission = "funnyguilds.invite",
        completer = "online-players:3",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(PluginConfiguration config, MessageConfiguration messages, Player player, @CanManage User user, Guild guild, String[] args) {
        when (args.length < 1, messages.generalNoNickGiven);
        when (guild.getMembers().size() >= config.maxMembersInGuild, messages.inviteAmount.replace("{AMOUNT}", Integer.toString(config.maxMembersInGuild)));

        User invitedUser = UserValidation.requireUserByName(args[0]);
        Player invitedPlayer = invitedUser.getPlayer();

        if (InvitationList.hasInvitationFrom(invitedUser, guild)) {
            if (!SimpleEventHandler.handle(new GuildMemberRevokeInviteEvent(EventCause.USER, user, guild, invitedUser))) {
                return;
            }
            
            InvitationList.expireInvitation(guild, invitedUser);
            player.sendMessage(messages.inviteCancelled);
            when (invitedPlayer != null, messages.inviteCancelledToInvited.replace("{OWNER}", player.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
            return;
        }

        when (invitedPlayer == null, messages.invitePlayerExists);
        when (invitedUser.hasGuild(), messages.generalUserHasGuild);

        if (!SimpleEventHandler.handle(new GuildMemberInviteEvent(EventCause.USER, user, guild, invitedUser))) {
            return;
        }
        
        InvitationList.createInvitation(guild, invitedPlayer);
        player.sendMessage(messages.inviteToOwner.replace("{PLAYER}", invitedPlayer.getName()));
        invitedPlayer.sendMessage(messages.inviteToInvited.replace("{OWNER}", player.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
    }

}
