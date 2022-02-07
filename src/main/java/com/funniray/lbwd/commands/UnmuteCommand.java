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
import com.funniray.lbwd.events.UnbanEvent;
import com.funniray.lbwd.utils.Colors;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

public class UnmuteCommand extends Command {

    public UnmuteCommand() {
        super("gunmute", CommandSettings.builder()
                .setDescription("mutes a player from the network")
                .setAliases(new String[]{"unmute","unipmute","gunipmute","gunmuteip","unmuteip"})
                .setPermission("litebans.unmute.own")
                .setUsageMessage("Please use /gunmute [-s] <Player>").build());
    }

    @Override
    public boolean onExecute(CommandSender commandSender, String s, String[] strings) {
        if (strings.length >= 1) {
            Ban ban;
            try {
                ban = Ban.createBan(commandSender,s==null?"gunmute":s,strings);
            } catch (Exception e) {
                commandSender.sendMessage(Colors.RED + "Something went wrong, the player you specified probably doesn't exist. Check the console.");
                e.printStackTrace();
                return true;
            }

            if (ban == null) {
                return true;
            }

            LBWD.datastore.removeMute(ban);

            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers().values()) {
                if (player.hasPermission("litebans.notify") || player.hasPermission("litebans.notify.broadcast") || player == commandSender) {
                    if (!ban.isSilent() || player.hasPermission("litebans.notify.silent")) {
                        player.sendMessage(Colors.GREEN + "Player " + ban.getName() + " was unmuted by " + ban.getRemovedByName());
                    }
                }
            }

            ProxyServer.getInstance().getLogger().info(Colors.GREEN + "Player " + ban.getName() + " was unmuted by " + ban.getRemovedByName());

            UnbanEvent event = new UnbanEvent(ban,"mute");
            ProxyServer.getInstance().getEventManager().callEvent(event);

            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(ban.getUuid());
            if (p!=null) {
                p.sendMessage(Colors.GREEN+"You've been unmuted!");
            }

            return true;
        } else {
            commandSender.sendMessage(getUsageMessage());
        }
        return false;
    }

}
