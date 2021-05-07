package com.funniray.lbwd.handlers;

import com.funniray.lbwd.LBWD;
import com.funniray.lbwd.datatypes.Ban;
import com.funniray.lbwd.utils.Colors;
import dev.waterdog.ProxyServer;
import dev.waterdog.event.defaults.PlayerLoginEvent;
import dev.waterdog.player.ProxiedPlayer;

import java.util.Set;
import java.util.function.Consumer;

public class LoginHandler implements Consumer<PlayerLoginEvent> {
    @Override
    public void accept(PlayerLoginEvent e) {
        if (e.getPlayer().hasPermission("litebans.notify.warned") && e.getPlayer().hasPermission("litebans.notify.warned.offline")) {
            Ban ban = LBWD.datastore.getActiveUnwarned(e.getPlayer().getUniqueId());

            //You can't send a message while the player is logging in, therefore, we wait a few seconds before warning them.
            //I would prefer to do this another way, but I don't see a better event :(

            if (ban != null) {
                Thread thread = new Thread(()->{
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    e.getPlayer().sendMessage(String.format(LBWD.getConfigString("WarnMessage"),ban.getReason(),ban.getBannedByName()));
                });

                thread.start();

                ban.setWarned(true);
                LBWD.datastore.setWarned(ban);
            }
        }


        Set<Ban> bans = LBWD.datastore.getAltsBanned(e.getPlayer().getUniqueId());

        if (bans.size() > 0) {
            for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers().values()) {
                if (p.hasPermission("litebans.notify") || p.hasPermission("litebans.notify.dupeip")) {
                    for(Ban ban : bans) {
                        p.sendMessage(Colors.GREEN + e.getPlayer().getName() + " is banned on account "+ban.getName()+ " for "+ban.getReason());
                    }
                }
            }

            for(Ban ban : bans) {
                ProxyServer.getInstance().getConsoleSender().sendMessage(Colors.GREEN + e.getPlayer().getName() + " is banned on account "+ban.getName()+ " for "+ban.getReason());
            }
        }
    }
}
