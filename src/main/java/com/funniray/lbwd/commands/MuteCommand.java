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
import dev.waterdog.ProxyServer;
import dev.waterdog.command.Command;
import dev.waterdog.command.CommandSender;
import dev.waterdog.command.CommandSettings;
import dev.waterdog.player.ProxiedPlayer;

import java.util.Date;


public class MuteCommand extends Command {

    public MuteCommand() {
        super("gmute", CommandSettings.builder()
                .setDescription("mutes a player from the network")
                .setAliases(new String[]{"mute","ipmute","gipmute","gmuteip","muteip","gtempmute","tempmute","tempipmute","gtempipmute","gtempmuteip","tempmuteip"})
                .setPermission("litebans.base")
                .setUsageMessage("Please use /<gmute | gipmute> [-s] <Player | UUID | IP> <Reason>").build());
    }

    @Override
    public boolean onExecute(CommandSender commandSender, String s, String[] strings) {
        if (strings.length >= 2) {
            Ban ban;
            try {
                ban = Ban.createBan(commandSender, s == null ? "gmute" : s, strings);
            } catch (Exception e) {
                commandSender.sendMessage(Colors.RED + "Something went wrong, the player you specified probably doesn't exist. Check the console.");
                e.printStackTrace();
                return true;
            }

            LBWD.datastore.addMute(ban);

            String message = String.format((ban.isSilent()?LBWD.getConfigString("SilentPrefix"):"")+LBWD.getConfigString("MuteBroadcast"),ban.getName(),ban.getBannedByName(),ban.getReason(),ban.getUntilString());

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
                if (p != null && p.hasPermission("litebans.notify.muted"))
                    p.sendMessage(String.format(LBWD.getConfigString("MuteMessage"),ban.getReason(),ban.getBannedByName(),ban.getUntilString()));
            } else {
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers().values()) {
                    if (player.getAddress().getAddress().getHostAddress().equals(ban.getIP()) && player.hasPermission("litebans.notify.muted")) {
                        player.sendMessage(String.format(LBWD.getConfigString("MuteMessage"),ban.getReason(),ban.getBannedByName(),ban.getUntilString()));
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