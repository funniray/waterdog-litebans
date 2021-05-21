/*
 *    WD-Litebans - A WaterDogPE plugin that attempts at being fully compatible with litebans
 *    Copyright (C) 2021  Funniray
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    I am available for any questions/requests: funniray10@gmail.com
 */

package com.funniray.lbwd.commands;

import com.funniray.lbwd.LBWD;
import com.funniray.lbwd.datatypes.Ban;
import com.funniray.lbwd.utils.Colors;
import com.funniray.lbwd.utils.UseQuotes;
import dev.waterdog.ProxyServer;
import dev.waterdog.command.Command;
import dev.waterdog.command.CommandSender;
import dev.waterdog.command.CommandSettings;
import dev.waterdog.player.ProxiedPlayer;


public class CheckBan extends Command {

    public CheckBan() {
        super("checkban", CommandSettings.builder()
                .setDescription("Checks if a player is banned")
                .setPermission("litebans.checkban")
                .setUsageMessage("Please use /checkban <Player | UUID | IP>").build());
    }

    @Override
    public boolean onExecute(CommandSender commandSender, String s, String[] strings) {
        strings = UseQuotes.parseArgs(strings);
        if (strings.length >= 1) {
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(strings[0]);
            Ban ban;

            if (p!=null) {
                ban = LBWD.datastore.getBan(p.getUniqueId().toString());
            } else {
                if (strings[0].contains("-") || strings[0].contains(":") || strings[0].contains(".")) {
                    ban = LBWD.datastore.getBan(strings[0]);
                } else {
                    ban = LBWD.datastore.getBan(LBWD.datastore.resolveName(strings[0]).toString());
                }
            }

            if (ban==null) {
                commandSender.sendMessage(Colors.RED+"This player is not banned");
            } else {
                commandSender.sendMessage(Colors.GREEN+"This player is banned by "+ban.getBannedByName()+" for "+ban.getUntilString()+" \""+ban.getReason()+"\"");
            }

            return true;
        } else {
            commandSender.sendMessage(getUsageMessage());
        }
        return false;
    }
}