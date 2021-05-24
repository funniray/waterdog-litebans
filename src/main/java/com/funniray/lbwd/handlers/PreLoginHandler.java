package com.funniray.lbwd.handlers;

import com.funniray.lbwd.LBWD;
import com.funniray.lbwd.datatypes.Ban;
import com.funniray.lbwd.utils.Colors;
import com.funniray.lbwd.utils.DateUtils;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.event.defaults.PlayerPreLoginEvent;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

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
