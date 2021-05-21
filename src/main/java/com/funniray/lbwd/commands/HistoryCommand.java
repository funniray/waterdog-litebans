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
import com.funniray.lbwd.utils.DateUtils;
import com.funniray.lbwd.utils.UseQuotes;
import dev.waterdog.ProxyServer;
import dev.waterdog.command.Command;
import dev.waterdog.command.CommandSender;
import dev.waterdog.command.CommandSettings;
import dev.waterdog.player.ProxiedPlayer;

import java.util.Date;
import java.util.Set;
import java.util.UUID;


public class HistoryCommand extends Command {

    public HistoryCommand() {
        super("history", CommandSettings.builder()
                .setDescription("Checks if a player is banned")
                .setPermission("litebans.history")
                .setUsageMessage("Please use /history <Player | UUID>").build());
    }

    @Override
    public boolean onExecute(CommandSender commandSender, String s, String[] strings) {
        strings = UseQuotes.parseArgs(strings);
        if (strings.length >= 1) {
            UUID uuid;

            if (strings[0].contains("-")) {
                uuid = UUID.fromString(strings[0]);
            } else {
                uuid = LBWD.datastore.resolveName(strings[0]);
            }

            if (uuid==null) {
                commandSender.sendMessage(Colors.RED+"That player cannot be found");
                return true;
            }

            Set<Ban> bans = LBWD.datastore.getHistoricalBans(uuid);
            Set<Ban> mutes = LBWD.datastore.getHistoricalMutes(uuid);
            Set<Ban> warns = LBWD.datastore.getHistoricalWarns(uuid);
            Set<Ban> kicks = LBWD.datastore.getHistoricalKicks(uuid);


            if (bans.size() > 0) {
                commandSender.sendMessage(Colors.DARK_RED + strings[0] + "'s bans:");
                for(Ban ban : bans) {
                    commandSender.sendMessage(Colors.GREEN + "Banned at "+Colors.AQUA+(new Date((long) ban.getTime())).toString() + Colors.GREEN + (ban.getUntil()>0?" for "+ Colors.DARK_AQUA + DateUtils.getDurationBreakdown((long) ((long) ban.getUntil()-ban.getTime())) +Colors.GREEN:"")+ " by "+Colors.BLUE+ban.getBannedByName() +Colors.GREEN+ " for "+Colors.LIGHT_PURPLE+ban.getReason()+Colors.GREEN + (ban.isActive()?"":" removed by "+Colors.GRAY+ban.getRemovedByName()));
                }
            }

            if (mutes.size() > 0) {
                commandSender.sendMessage(Colors.DARK_RED + strings[0] + "'s mutes:");
                for(Ban ban : mutes) {
                    commandSender.sendMessage(Colors.GREEN + "Muted at "+Colors.AQUA+(new Date((long) ban.getTime())).toString() + Colors.GREEN + (ban.getUntil()>0?" for "+ Colors.DARK_AQUA + DateUtils.getDurationBreakdown((long) ((long) ban.getUntil()-ban.getTime())) +Colors.GREEN:"")+ " by "+Colors.BLUE+ban.getBannedByName() +Colors.GREEN+ " for "+Colors.LIGHT_PURPLE+ban.getReason()+Colors.GREEN + (ban.isActive()?"":" removed by "+Colors.GRAY+ban.getRemovedByName()));
                }
            }

            if (warns.size() > 0) {
                commandSender.sendMessage(Colors.DARK_RED + strings[0] + "'s warns:");
                for(Ban ban : warns) {
                    commandSender.sendMessage(Colors.GREEN + "Warned at "+Colors.AQUA+(new Date((long) ban.getTime())).toString() + Colors.GREEN + (ban.getUntil()>0?" for "+ Colors.DARK_AQUA + DateUtils.getDurationBreakdown((long) ((long) ban.getUntil()-ban.getTime())) +Colors.GREEN:"")+ " by "+Colors.BLUE+ban.getBannedByName() +Colors.GREEN+ " for "+Colors.LIGHT_PURPLE+ban.getReason()+Colors.GREEN + (ban.isActive()?"":" removed by "+Colors.GRAY+ban.getRemovedByName()));
                }
            }

            if (kicks.size() > 0) {
                commandSender.sendMessage(Colors.DARK_RED + strings[0] + "'s kicks:");
                for(Ban ban : kicks) {
                    commandSender.sendMessage(Colors.GREEN + "Kicked at "+Colors.AQUA+(new Date((long) ban.getTime())).toString() + Colors.GREEN + (ban.getUntil()>0?" for "+ Colors.DARK_AQUA + DateUtils.getDurationBreakdown((long) ((long) ban.getUntil()-ban.getTime())) +Colors.GREEN:"")+ " by "+Colors.BLUE+ban.getBannedByName() +Colors.GREEN+ " for "+Colors.LIGHT_PURPLE+ban.getReason()+Colors.GREEN + (ban.isActive()?"":" removed by "+Colors.GRAY+ban.getRemovedByName()));
                }
            }

            return true;
        } else {
            commandSender.sendMessage(getUsageMessage());
        }
        return false;
    }
}