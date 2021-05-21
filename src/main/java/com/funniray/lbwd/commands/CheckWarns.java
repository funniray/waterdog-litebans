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