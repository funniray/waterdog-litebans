package com.funniray.lbwd;

import com.funniray.lbwd.Datastores.Datastore;
import com.funniray.lbwd.Datastores.SQLStore;
import com.funniray.lbwd.commands.*;
import com.funniray.lbwd.handlers.LoginHandler;
import com.funniray.lbwd.handlers.PreLoginHandler;
import com.funniray.lbwd.handlers.MessageHandler;
import dev.waterdog.waterdogpe.event.defaults.PlayerChatEvent;
import dev.waterdog.waterdogpe.event.defaults.PlayerLoginEvent;
import dev.waterdog.waterdogpe.event.defaults.PlayerPreLoginEvent;
import dev.waterdog.waterdogpe.plugin.Plugin;
import dev.waterdog.waterdogpe.utils.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class LBWD extends Plugin {

    public static Datastore datastore;
    private static Configuration config;

    //TODO: Staffhistory

    private void createDefaultConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");


        if (!file.exists()) {
            try (InputStream in = getResourceFile("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getConfigString(String str) {
        return config.getString(str);
    }

    @Override
    public void onEnable() {

        createDefaultConfig();

        LBWD.config = getConfig();

        datastore = new SQLStore(config.getString("jdbcurl"),config.getString("dbusername"),config.getString("dbpassword"));

        getProxy().getCommandMap().registerCommand(new BanCommand());
        getProxy().getCommandMap().registerCommand(new PardonCommand());
        getProxy().getCommandMap().registerCommand(new MuteCommand());
        getProxy().getCommandMap().registerCommand(new UnmuteCommand());
        getProxy().getCommandMap().registerCommand(new WarnCommand());
        getProxy().getCommandMap().registerCommand(new KickCommand());

        getProxy().getCommandMap().registerCommand(new CheckBan());
        getProxy().getCommandMap().registerCommand(new CheckMute());
        getProxy().getCommandMap().registerCommand(new CheckWarns());

        getProxy().getCommandMap().registerCommand(new HistoryCommand());
        getProxy().getCommandMap().registerCommand(new AltsCommand());

        getProxy().getEventManager().subscribe(PlayerPreLoginEvent.class, new PreLoginHandler());
        getProxy().getEventManager().subscribe(PlayerLoginEvent.class, new LoginHandler());
        getProxy().getEventManager().subscribe(PlayerChatEvent.class, new MessageHandler());


    }


}