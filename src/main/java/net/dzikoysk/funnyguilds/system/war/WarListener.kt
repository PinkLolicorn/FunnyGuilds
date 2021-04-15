package net.dzikoysk.funnyguilds.system.war;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.command.user.InfoCommand;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.system.security.SecuritySystem;
import net.dzikoysk.funnyguilds.util.nms.GuildEntityHelper;
import net.dzikoysk.funnyguilds.util.nms.Reflections;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class WarListener {

    private static final InfoCommand INFO_EXECUTOR = new InfoCommand();
    
    private static final Class<?> USE_ENTITY_CLASS;
    private static final Field PACKET_ID_FIELD;
    private static final Field PACKET_ACTION_FIELD;
    private static final Field ENUM_HAND_FIELD;

    static {
        USE_ENTITY_CLASS = Reflections.getNMSClass("PacketPlayInUseEntity");
        PACKET_ID_FIELD = Reflections.getPrivateField(USE_ENTITY_CLASS, "a");
        PACKET_ACTION_FIELD = Reflections.getPrivateField(USE_ENTITY_CLASS, "action");
        ENUM_HAND_FIELD = Reflections.SERVER_VERSION.startsWith("v1_8") ? null : Reflections.getPrivateField(USE_ENTITY_CLASS, "d");
    }

    private WarListener() {}

    public static void use(Player player, Object packet) {
        try {
            if (packet == null) {
                return;
            }

            if (!packet.getClass().equals(USE_ENTITY_CLASS)) {
                return;
            }

            if (PACKET_ACTION_FIELD == null) {
                return;
            }

            int id = PACKET_ID_FIELD.getInt(packet);
            Object actionEnum = PACKET_ACTION_FIELD.get(packet);
            Object enumHand = "";

            if (ENUM_HAND_FIELD != null) {
                enumHand = ENUM_HAND_FIELD.get(packet);
            }

            if (actionEnum == null) {
                return;
            }

            call(player, id, actionEnum.toString(), enumHand == null ? "" : enumHand.toString());
        } catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("WarListener error", exception);
        }
    }

    private static void call(Player player, int id, String action, String hand) {
        for (Map.Entry<Guild, Integer> entry : GuildEntityHelper.getGuildEntities().entrySet()) {
            if (!entry.getValue().equals(id)) {
                continue;
            }

            Guild guild = entry.getKey();

            if (SecuritySystem.onHitCrystal(player, guild)) {
                return;
            }

            if ("ATTACK".equalsIgnoreCase(action)) {
                WarSystem.getInstance().attack(player, entry.getKey());
                return;
            }

            if ("INTERACT_AT".equalsIgnoreCase(action)) {
                PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
                
                if (config.informationMessageCooldowns.cooldown(player, TimeUnit.SECONDS, config.infoPlayerCooldown)) {
                    return;
                }

                if (!hand.isEmpty() && !"MAIN_HAND".equalsIgnoreCase(hand)) {
                    return;
                }

                try {
                    INFO_EXECUTOR.execute(config, FunnyGuilds.getInstance().getMessageConfiguration(), player, new String[]{ entry.getKey().getTag() });
                } catch (ValidationException validatorException) {
                    validatorException.getValidationMessage().peek(player::sendMessage);
                }
            }
        }
    }

}
