package com.funniray.lbwd.commands;

import com.funniray.lbwd.LBWD;
import com.funniray.lbwd.datatypes.Ban;
import com.funniray.lbwd.utils.Colors;
import com.funniray.lbwd.utils.UseQuotes;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;



public class CheckMute extends Command {

    public CheckMute() {
        super("checkmute", CommandSettings.builder()
                .setDescription("Checks if a player is muted")
                .setPermission("litebans.checkmute")
                .setUsageMessage("Please use /checkmute <Player | UUID | IP>").build());
    }

    @Override
    public boolean onExecute(CommandSender commandSender, String s, String[] strings) {
        strings = UseQuotes.parseArgs(strings);
        if (strings.length >= 1) {
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(strings[0]);
            Ban ban;

            if (p!=null) {
                ban = LBWD.datastore.getMute(p.getUniqueId().toString());
            } else {
                if (strings[0].contains("-") || strings[0].contains(":") || strings[0].contains(".")) {
                    ban = LBWD.datastore.getMute(strings[0]);
                } else {
                    ban = LBWD.datastore.getMute(LBWD.datastore.resolveName(strings[0]).toString());
                }
            }

            if (ban==null) {
                commandSender.sendMessage(Colors.RED+"This player is not muted");
            } else {
                commandSender.sendMessage(Colors.GREEN+"This player is muted by "+ban.getBannedByName()+" for "+ban.getUntilString()+" \""+ban.getReason()+"\"");
            }

            return true;
        } else {
            commandSender.sendMessage(getUsageMessage());
        }
        return false;
    }
}