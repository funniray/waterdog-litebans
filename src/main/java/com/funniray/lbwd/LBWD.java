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

        if (getConfig().getBoolean("showpiracywarning")) {
            this.getLogger().critical("A WaterDogPE plugin that attempts at being fully compatible with litebans\n" +
                "A free plugin by Funniray. https://github.com/funniray/waterdog-litebans If you paid for this plugin, you were scammed.\n" +
                "Report resellers to beingscammed@reallyisnt.fun");
        }

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