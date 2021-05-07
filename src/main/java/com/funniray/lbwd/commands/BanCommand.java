package com.funniray.lbwd.commands;

import com.funniray.lbwd.LBWD;
import com.funniray.lbwd.datatypes.Ban;
import com.funniray.lbwd.utils.Colors;
import com.funniray.lbwd.utils.DateUtils;
import dev.waterdog.command.Command;
import dev.waterdog.command.CommandSender;
import dev.waterdog.ProxyServer;
import dev.waterdog.command.CommandSettings;
import dev.waterdog.player.ProxiedPlayer;

import java.util.Date;


public class BanCommand extends Command {

    public BanCommand() {
        super("gban", CommandSettings.builder()
                .setDescription("Bans a player from the network")
                .setAliases(new String[]{"ban","ipban","gipban","gbanip","banip","gtempban","tempban","tempipban","gtempipban","gtempbanip","tempbanip"})
                .setPermission("litebans.base")
                .setUsageMessage("Please use /<gban | gipban> [-s] <Player | UUID | IP> <Reason>").build());
    }

    @Override
    public boolean onExecute(CommandSender commandSender, String s, String[] strings) {
        if (strings.length >= 2) {
            Ban ban;
            try {
                ban = Ban.createBan(commandSender,s==null?"gban":s,strings);
            } catch (Exception e) {
                commandSender.sendMessage(Colors.RED + "Something went wrong, the player you specified probably doesn't exist. Check the console.");
                e.printStackTrace();
                return true;
            }

            LBWD.datastore.addBan(ban);

            String message = String.format((ban.isSilent()?LBWD.getConfigString("SilentPrefix"):"")+LBWD.getConfigString("BanBroadcast"),ban.getName(),ban.getBannedByName(),ban.getReason(),ban.getUntilString());

            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers().values()) {
                if (player.hasPermission("litebans.notify") || player.hasPermission("litebans.notify.broadcast") || player == commandSender) {
                    if (!ban.isSilent() || player.hasPermission("litebans.notify.silent")) {
                        player.sendMessage(message);
                    }
                }
            }

            ProxyServer.getInstance().getLogger().info(message);

            if (!ban.isIpban()) {
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(ban.getUuid());
                if (p != null)
                    p.disconnect(String.format(LBWD.getConfigString("BanMessage"),ban.getReason(),ban.getBannedByName(),ban.getUntilString()));
            } else {
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers().values()) {
                    if (player.getAddress().getAddress().getHostAddress().equals(ban.getIP())) {
                        player.disconnect(String.format(LBWD.getConfigString("BanMessage"),ban.getReason(),ban.getBannedByName(),ban.getUntilString()));
                    }
                }
            }

            return true;
        } else {
            commandSender.sendMessage(getUsageMessage());
        }
        return false;
    }
}