package com.funniray.lbwd.commands;

import com.funniray.lbwd.LBWD;
import com.funniray.lbwd.datatypes.Ban;
import com.funniray.lbwd.utils.Colors;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

public class UnmuteCommand extends Command {

    public UnmuteCommand() {
        super("gunmute", CommandSettings.builder()
                .setDescription("mutes a player from the network")
                .setAliases(new String[]{"unmute","unipmute","gunipmute","gunmuteip","unmuteip"})
                .setPermission("litebans.unmute.own")
                .setUsageMessage("Please use /gunmute [-s] <Player>").build());
    }

    @Override
    public boolean onExecute(CommandSender commandSender, String s, String[] strings) {
        if (strings.length >= 1) {
            Ban ban;
            try {
                ban = Ban.createBan(commandSender,s==null?"gunmute":s,strings);
            } catch (Exception e) {
                commandSender.sendMessage(Colors.RED + "Something went wrong, the player you specified probably doesn't exist. Check the console.");
                e.printStackTrace();
                return true;
            }

            LBWD.datastore.removeMute(ban);

            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers().values()) {
                if (player.hasPermission("litebans.notify") || player.hasPermission("litebans.notify.broadcast") || player == commandSender) {
                    if (!ban.isSilent() || player.hasPermission("litebans.notify.silent")) {
                        player.sendMessage(Colors.GREEN + "Player " + ban.getName() + " was unmuted by " + ban.getRemovedByName());
                    }
                }
            }

            ProxyServer.getInstance().getLogger().info(Colors.GREEN + "Player " + ban.getName() + " was unmuted by " + ban.getRemovedByName());

            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(ban.getUuid());
            if (p!=null) {
                p.sendMessage(Colors.GREEN+"You've been unmuted!");
            }

            return true;
        } else {
            commandSender.sendMessage(getUsageMessage());
        }
        return false;
    }

}
