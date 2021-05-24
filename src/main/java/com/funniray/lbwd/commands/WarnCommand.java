package com.funniray.lbwd.commands;

import com.funniray.lbwd.LBWD;
import com.funniray.lbwd.datatypes.Ban;
import com.funniray.lbwd.utils.Colors;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;


public class WarnCommand extends Command {

    public WarnCommand() {
        super("gwarn", CommandSettings.builder()
                .setDescription("Warns a player on the network")
                .setAliases(new String[]{"warn"})
                .setPermission("litebans.base")
                .setUsageMessage("Please use /<gwarn> [-s] <Player | UUID | IP> <Reason>").build());
    }

    @Override
    public boolean onExecute(CommandSender commandSender, String s, String[] strings) {
        if (strings.length >= 2) {
            Ban ban;
            try {
                ban = Ban.createBan(commandSender,"warn",strings);
            } catch (Exception e) {
                commandSender.sendMessage(Colors.RED + "Something went wrong, the player you specified probably doesn't exist. Check the console.");
                e.printStackTrace();
                return true;
            }

            LBWD.datastore.addWarn(ban);

            String message = String.format((ban.isSilent()?LBWD.getConfigString("SilentPrefix"):"")+LBWD.getConfigString("WarnBroadcast"),ban.getName(),ban.getBannedByName(),ban.getReason());

            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers().values()) {
                if (player.hasPermission("litebans.notify") || player.hasPermission("litebans.notify.broadcast") || player == commandSender) {
                    if (!ban.isSilent() || player.hasPermission("litebans.notify.silent")) {
                        player.sendMessage(message);
                    }
                }
            }

            ProxyServer.getInstance().getLogger().info(message);

            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(ban.getUuid());
            if (p != null && p.hasPermission("litebans.notify.warned")) {
                p.sendMessage(String.format(LBWD.getConfigString("WarnMessage"),ban.getReason(),ban.getBannedByName()));
                ban.setWarned(true);

                LBWD.datastore.setWarned(ban);
            }

            return true;
        } else {
            commandSender.sendMessage(getUsageMessage());
        }
        return false;
    }
}