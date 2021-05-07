package com.funniray.lbwd.commands;

import com.funniray.lbwd.LBWD;
import com.funniray.lbwd.datatypes.Ban;
import com.funniray.lbwd.utils.Colors;
import dev.waterdog.ProxyServer;
import dev.waterdog.command.Command;
import dev.waterdog.command.CommandSender;
import dev.waterdog.command.CommandSettings;
import dev.waterdog.player.ProxiedPlayer;

public class PardonCommand extends Command {

    public PardonCommand() {
        super("gpardon", CommandSettings.builder()
                .setDescription("Bans a player from the network")
                .setAliases(new String[]{"pardon","unban","unipban","gunipban","gunbanip","unbanip"})
                .setPermission("litebans.unban.own")
                .setUsageMessage("Please use /gpardon [-s] <Player>").build());
    }

    @Override
    public boolean onExecute(CommandSender commandSender, String s, String[] strings) {
        if (strings.length >= 1) {
            Ban ban;
            try {
                ban = Ban.createBan(commandSender,s==null?"gpardon":s,strings);
            } catch (Exception e) {
                commandSender.sendMessage(Colors.RED + "Something went wrong, the player you specified probably doesn't exist. Check the console.");
                e.printStackTrace();
                return true;
            }

            LBWD.datastore.removeBan(ban);

            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers().values()) {
                if (player.hasPermission("litebans.notify") || player.hasPermission("litebans.notify.broadcast") || player == commandSender) {
                    if (!ban.isSilent() || player.hasPermission("litebans.notify.silent")) {
                        player.sendMessage(Colors.GREEN + "Player " + ban.getName() + " was pardoned by " + ban.getRemovedByName());
                    }
                }
            }

            ProxyServer.getInstance().getLogger().info(Colors.GREEN + "Player " + ban.getName() + " was pardoned by " + ban.getRemovedByName());

            return true;
        } else {
            commandSender.sendMessage(getUsageMessage());
        }
        return false;
    }

}