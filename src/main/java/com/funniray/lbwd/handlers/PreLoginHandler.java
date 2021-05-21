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
import com.funniray.lbwd.utils.DateUtils;
import dev.waterdog.ProxyServer;
import dev.waterdog.event.defaults.PlayerPreLoginEvent;
import dev.waterdog.player.ProxiedPlayer;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class PreLoginHandler implements Consumer<PlayerPreLoginEvent> {
    @Override
    public void accept(PlayerPreLoginEvent e) {
        LBWD.datastore.addHistory(e.getLoginData().getDisplayName(), e.getLoginData().getUuid(), e.getAddress().getAddress().getHostAddress());

        UUID uuid = e.getLoginData().getUuid();

        Set<Ban> bans = LBWD.datastore.getActiveBans(uuid.toString(), false);

        Set<Ban> ipbans = LBWD.datastore.getActiveBans(e.getAddress().getAddress().getHostAddress(), true);

        if (bans.size() > 0) {
            Ban ban = (Ban) bans.toArray()[0];
            e.setCancelReason(String.format(LBWD.getConfigString("BanMessage"),ban.getReason(),ban.getBannedByName(),ban.getUntilString()));
            e.setCancelled(true);

            for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers().values()) {
                if (p.hasPermission("litebans.notify.banned_join") || p.hasPermission("litebans.notify") ) {
                    p.sendMessage(String.format(LBWD.getConfigString("BanJoinMessage"),e.getLoginData().getDisplayName()));
                }
            }

            ProxyServer.getInstance().getLogger().info(String.format(LBWD.getConfigString("BanJoinMessage"),e.getLoginData().getDisplayName()));
        } else if (ipbans.size() > 0) {
            Ban ban = (Ban) ipbans.toArray()[0];
            e.setCancelReason(String.format(LBWD.getConfigString("BanMessage"),ban.getReason(),ban.getBannedByName(),ban.getUntilString()));
            e.setCancelled(true);

            for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers().values()) {
                if (p.hasPermission("litebans.notify.banned_join") || p.hasPermission("litebans.notify") ) {
                    p.sendMessage(String.format(LBWD.getConfigString("BanIPJoinMessage"),e.getLoginData().getDisplayName()));
                }
            }

            ProxyServer.getInstance().getLogger().info(String.format(LBWD.getConfigString("BanIPJoinMessage"),e.getLoginData().getDisplayName()));
        }
    }
}
