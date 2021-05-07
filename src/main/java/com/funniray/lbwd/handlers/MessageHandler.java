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
