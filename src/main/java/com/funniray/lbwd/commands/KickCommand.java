package com.funniray.lbwd.commands;

import com.funniray.lbwd.LBWD;
import com.funniray.lbwd.datatypes.Ban;
import com.funniray.lbwd.utils.Colors;
import com.funniray.lbwd.utils.DateUtils;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

import java.util.Date;


public class KickCommand extends Command {

    public KickCommand() {
        super("gkick", CommandSettings.builder()
                .setDescription("kicks a player from the network")
                .setAliases(new String[]{"kick","ipkick","gipkick","gkickip","kickip"})
                .setPermission("litebans.base")
                .setUsageMessage("Please use /<gkick | gipkick> [-s] <Player | UUID | IP> <Reason>").build());
    }

    @Override
    public boolean onExecute(CommandSender commandSender, String s, String[] strings) {
        if (strings.length >= 2) {
            Ban ban;
            try {
                ban = Ban.createBan(commandSender,s==null?"gkick":s,strings);
            } catch (Exception e) {
                commandSender.sendMessage(Colors.RED + "Something went wrong, the player you specified probably doesn't exist. Check the console.");
                e.printStackTrace();
                return true;
            }

            LBWD.datastore.addKick(ban);

            String message = String.format((ban.isSilent()?LBWD.getConfigString("SilentPrefix"):"")+LBWD.getConfigString("KickBroadcast"),ban.getName(),ban.getBannedByName(),ban.getReason());

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
                    p.disconnect(String.format(LBWD.getConfigString("KickMessage"),ban.getReason(),ban.getBannedByName()));
            } else {
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers().values()) {
                    if (player.getAddress().getAddress().getHostAddress().equals(ban.getIP())) {
                        player.disconnect(String.format(LBWD.getConfigString("KickMessage"),ban.getReason(),ban.getBannedByName()));
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