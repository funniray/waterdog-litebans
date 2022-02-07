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

package com.funniray.lbwd.Datastores;

import com.funniray.lbwd.datatypes.Alt;
import com.funniray.lbwd.datatypes.Ban;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.UUID;

public interface Datastore {

    HashSet<Ban> getHistoricalBans(UUID uuid);
    HashSet<Ban> getHistoricalMutes(UUID uuid);
    HashSet<Ban> getHistoricalWarns(UUID uuid);
    HashSet<Ban> getHistoricalKicks(UUID uuid);
    HashSet<Ban> getActiveBans(String uuid, boolean ip);
    HashSet<Ban> getActiveBans(String uuid, String node, boolean ip);
    HashSet<Ban> getActiveMutes(UUID uuid);
    HashSet<Ban> getActiveWarns(String uuid);

    void createServerIfNotExists(UUID uuid, String name);
    void getServer(String name);

    void addHistory(String name, UUID uuid, String ip);

    void addBan(Ban ban);
    void removeBan(Ban ban);
    Ban getBan(String uuid);

    void addMute(Ban ban);
    void removeMute(Ban ban);
    Ban getMute(String uuid);

    void addKick(Ban ban);

    void addWarn(Ban ban);
    Ban getActiveUnwarned(UUID uuid);
    void setWarned(Ban ban);

    HashSet<Alt> getAlts(UUID uuid);
    HashSet<Ban> getAltsBanned(UUID uuid);

    String resolveUUID(UUID uuid);
    UUID resolveName(String name);
    String resolveUUIDToIp(UUID uuid);

    void close();


}
