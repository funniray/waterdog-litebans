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


}
