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

import java.util.Set;


public class CheckWarns extends Command {

    public CheckWarns() {
        super("warnings", CommandSettings.builder()
                .setDescription("Checks if a player is banned")
                .setPermission("litebans.warnings.self")
                .setUsageMessage("Please use /warnings [Player | UUID | IP]").build());
    }

    @Override
    public boolean onExecute(CommandSender commandSender, String s, String[] strings) {
        strings = UseQuotes.parseArgs(strings);
        ProxiedPlayer p;
        if (strings.length >= 1) {
            commandSender.hasPermission("litebans.warnings");
            p = ProxyServer.getInstance().getPlayer(strings[0]);
        } else if (commandSender.isPlayer()) {
            p = (ProxiedPlayer) commandSender;
        } else {
            commandSender.sendMessage("You must supply an argument.");
            return true;
        }

        Set<Ban> bans;

        if (p!=null) {
            bans = LBWD.datastore.getActiveWarns(p.getUniqueId().toString());
        } else {
            if (strings[0].contains("-") || strings[0].contains(":") || strings[0].contains(".")) {
                bans = LBWD.datastore.getActiveWarns(strings[0]);
            } else {
                bans = LBWD.datastore.getActiveWarns(LBWD.datastore.resolveName(strings[0]).toString());
            }
        }

        if (bans.size()<1) {
            commandSender.sendMessage(Colors.RED+"This player is not banned");
        } else {
            for(Ban ban : bans)
                commandSender.sendMessage("["+ban.getId()+"]"+Colors.GREEN+" This player was warned by "+ban.getBannedByName()+" for "+ban.getReason());
        }

        return true;
    }
}