package com.funniray.lbwd.commands;

import com.funniray.lbwd.LBWD;
import com.funniray.lbwd.datatypes.Alt;
import com.funniray.lbwd.utils.Colors;
import com.funniray.lbwd.utils.UseQuotes;
import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;

import java.util.Set;
import java.util.UUID;

public class AltsCommand extends Command {
    public AltsCommand() {
        super("alts", CommandSettings.builder()
                .setDescription("Bans a player from the network")
                .setAliases(new String[]{"checkip"})
                .setPermission("litebans.base")
                .setUsageMessage("Please use /checkip <Player | UUID>").build());
    }

    @Override
    public boolean onExecute(CommandSender commandSender, String s, String[] strings) {
        strings = UseQuotes.parseArgs(strings);
        if (strings.length >= 1) {
            Set<Alt> alts;

            if (strings[0].contains("-")) {
                alts = LBWD.datastore.getAlts(UUID.fromString(strings[0]));
            } else {
                alts = LBWD.datastore.getAlts(LBWD.datastore.resolveName(strings[0]));
            }

            if (alts.size() > 0) {
                commandSender.sendMessage(Colors.GREEN+ "That player has also gone by");
                StringBuilder sb = new StringBuilder(Colors.GREEN.toString());
                for (Alt alt : alts) {
                    sb.append(alt.getName());
                    sb.append(", ");
                }
                commandSender.sendMessage(sb.toString());
            } else {
                commandSender.sendMessage(Colors.RED + "Couldn't find that player");
            }
        } else {
            return false;
        }
        return true;
    }
}
