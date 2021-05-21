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

package com.funniray.lbwd.handlers;

import com.funniray.lbwd.LBWD;
import com.funniray.lbwd.datatypes.Ban;
import com.funniray.lbwd.utils.Colors;
import dev.waterdog.ProxyServer;
import dev.waterdog.event.defaults.PlayerChatEvent;
import dev.waterdog.player.ProxiedPlayer;

import java.util.function.Consumer;

public class MessageHandler implements Consumer<PlayerChatEvent> {
    @Override
    public void accept(PlayerChatEvent e) {
        Ban ban;

        ban = LBWD.datastore.getMute(e.getPlayer().getAddress().getAddress().getHostAddress());

        if (ban == null)
            ban = LBWD.datastore.getMute(e.getPlayer().getUniqueId().toString());

        if (ban == null || !ban.isActive())
            return;

        e.getPlayer().sendMessage(Colors.RED + "You are currently muted for "+ban.getUntilString()+" \""+ban.getReason()+"\"");
        e.setCancelled();

        String message = "["+Colors.RED+"MUTED"+Colors.WHITE+"] "+e.getPlayer().getName()+": "+e.getMessage();

        for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers().values()) {
            if (p.hasPermission("litebans.notify.mute")) {
                p.sendMessage(message);
            }
        }

        ProxyServer.getInstance().getLogger().info(message);
    }
}
