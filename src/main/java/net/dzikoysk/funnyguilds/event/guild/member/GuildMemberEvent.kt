package net.dzikoysk.funnyguilds.event.guild.member

import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.event.guild.GuildEvent

net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.element.gui.GuiActionHandler
import net.dzikoysk.funnyguilds.listener.EntityDamage
import net.dzikoysk.funnyguilds.listener.EntityInteract
import net.dzikoysk.funnyguilds.listener.PlayerChat
import net.dzikoysk.funnyguilds.listener.PlayerDeath
import net.dzikoysk.funnyguilds.listener.PlayerJoin
import net.dzikoysk.funnyguilds.listener.PlayerLogin
import net.dzikoysk.funnyguilds.listener.PlayerQuit
import net.dzikoysk.funnyguilds.listener.TntProtection
import net.dzikoysk.funnyguilds.listener.BlockFlow
import net.dzikoysk.funnyguilds.listener.region.EntityPlace
import net.dzikoysk.funnyguilds.listener.region.BlockBreak
import net.dzikoysk.funnyguilds.listener.region.BlockIgnite
import net.dzikoysk.funnyguilds.listener.region.BucketAction
import net.dzikoysk.funnyguilds.listener.region.EntityExplode
import net.dzikoysk.funnyguilds.listener.region.HangingBreak
import net.dzikoysk.funnyguilds.listener.region.HangingPlace
import net.dzikoysk.funnyguilds.listener.region.PlayerCommand
import net.dzikoysk.funnyguilds.listener.region.PlayerInteract
import net.dzikoysk.funnyguilds.listener.region.EntityProtect
import net.dzikoysk.funnyguilds.listener.region.PlayerMove
import net.dzikoysk.funnyguilds.listener.region.BlockPhysics
import net.dzikoysk.funnyguilds.listener.region.PlayerRespawn
import java.lang.StackTraceElement

abstract class GuildMemberEvent(eventCause: EventCause?, doer: User?, guild: Guild?, val member: User) : GuildEvent(eventCause, doer, guild)