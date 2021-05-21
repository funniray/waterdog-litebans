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
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.*;

public class SQLStore implements Datastore {

    private static final String table_prefix = "litebans_";

    private static final String CREATE_BANS = "CREATE TABLE IF NOT EXISTS "+table_prefix+"bans ( id SERIAL, uuid varchar(36), ip varchar(45), reason varchar(2048), banned_by_uuid varchar(36), banned_by_name varchar(128), removed_by_uuid varchar(36), removed_by_name varchar(128), removed_by_date timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, time bigint(20), until bigint(20), server_scope varchar(32), server_origin varchar(32), silent bit(1), ipban bit(1), active bit(1) );";
    private static final String CREATE_MUTES = "CREATE TABLE IF NOT EXISTS "+table_prefix+"mutes ( id SERIAL, uuid varchar(36), ip varchar(45), reason varchar(2048), banned_by_uuid varchar(36), banned_by_name varchar(128), removed_by_uuid varchar(36), removed_by_name varchar(128), removed_by_date timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, time bigint(20), until bigint(20), server_scope varchar(32), server_origin varchar(32), silent bit(1), ipban bit(1), active bit(1) );";
    private static final String CREATE_WARNS = "CREATE TABLE IF NOT EXISTS "+table_prefix+"warnings ( id SERIAL, uuid varchar(36), ip varchar(45), reason varchar(2048), banned_by_uuid varchar(36), banned_by_name varchar(128), removed_by_uuid varchar(36), removed_by_name varchar(128), removed_by_date timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, time bigint(20), until bigint(20), server_scope varchar(32), server_origin varchar(32), silent bit(1), ipban bit(1), active bit(1), warned bit(1) );";
    private static final String CREATE_KICKS = "CREATE TABLE IF NOT EXISTS "+table_prefix+"kicks ( id SERIAL, uuid varchar(36), ip varchar(45), reason varchar(2048), banned_by_uuid varchar(36), banned_by_name varchar(128), time bigint(20), until bigint(20), server_scope varchar(32), server_origin varchar(32), silent bit(1), ipban bit(1), active bit(1) );";
    private static final String CREATE_CONFIG = "CREATE TABLE IF NOT EXISTS "+table_prefix+"config (id SERIAL, version varchar(128), build varchar(128), timezone varchar(64) DEFAULT '+00:00');";
    private static final String CREATE_VERSION = "INSERT INTO "+table_prefix+"config (id, version, build) VALUES (1, '2021-04-13@07:15', 482) ON DUPLICATE KEY UPDATE version = '2021-04-13@07:15',build = 482;";
    private static final String CREATE_HISTORY = "CREATE TABLE IF NOT EXISTS "+table_prefix+"history (id SERIAL, uuid varchar(36), ip varchar(45), name varchar(128), date timestamp DEFAULT CURRENT_TIMESTAMP);";

    public static final String GET_BANS_BY_UUID_HISTORICAL = "SELECT *, false as warned FROM "+table_prefix+"bans WHERE UUID=?;";
    public static final String GET_MUTES_BY_UUID_HISTORICAL = "SELECT *, false as warned FROM "+table_prefix+"mutes WHERE UUID=?;";
    public static final String GET_WARNS_BY_UUID_HISTORICAL = "SELECT * FROM "+table_prefix+"warnings WHERE UUID=?;";
    public static final String GET_KICKS_BY_UUID_HISTORICAL = "SELECT *, false as warned, \"00000000-0000-0000-0000-000000000000\" as removed_by_uuid, null as removed_by_name, null as removed_by_date FROM "+table_prefix+"kicks WHERE UUID=?;";

    public static final String GET_BANS_BY_UUID_ACTIVE = "SELECT *, false AS warned FROM "+table_prefix+"bans WHERE uuid=? AND ipban=false AND active=true AND (until>=(UNIX_TIMESTAMP()*1000) OR until=-1);";
    public static final String GET_SOMETHING_BY_UUID_ACTIVE = "SELECT *, false AS warned FROM "+table_prefix+"%node%s WHERE uuid=? AND ipban=false AND active=true AND (until>=(UNIX_TIMESTAMP()*1000) OR until=-1);";
    public static final String GET_SOMETHING_BY_IP_ACTIVE = "SELECT *, false AS warned FROM "+table_prefix+"%node%s WHERE ip=? AND ipban=TRUE AND active=true AND (until>=(UNIX_TIMESTAMP()*1000) OR until=-1);";

    public static final String ADD_BAN = "INSERT INTO "+table_prefix+"bans (uuid, ip, reason, banned_by_uuid, banned_by_name, time, until, silent, ipban, active, server_origin, server_scope) VALUES (?,?,?,?,?,?,?,?,?,true,'proxy','*')";
    public static final String REMOVE_BAN = "UPDATE "+table_prefix+"bans SET removed_by_uuid=?, removed_by_name=?, active=FALSE WHERE id=?;";

    public static final String ADD_MUTE = "INSERT INTO "+table_prefix+"mutes (uuid, ip, reason, banned_by_uuid, banned_by_name, time, until, silent, ipban, active, server_origin, server_scope) VALUES (?,?,?,?,?,?,?,?,?,true,'proxy','*')";
    public static final String REMOVE_MUTE = "UPDATE "+table_prefix+"mutes SET removed_by_uuid=?, removed_by_name=?, active=FALSE WHERE id=?;";

    public static final String ADD_KICK = "INSERT INTO "+table_prefix+"kicks (uuid, ip, reason, banned_by_uuid, banned_by_name, time, until, silent, ipban, active, server_origin, server_scope) VALUES (?,?,?,?,?,?,?,?,?,true,'proxy','*')";

    public static final String ADD_WARN = "INSERT INTO "+table_prefix+"warnings (uuid, ip, reason, banned_by_uuid, banned_by_name, time, until, silent, ipban, active, server_origin, server_scope, warned) VALUES (?,?,?,?,?,?,?,?,?,true,'proxy','*', false)";
    public static final String GET_ACTIVE_UNWARNED = "SELECT * FROM "+table_prefix+"warnings WHERE uuid=? AND active=TRUE and (NOT WARNED=true);";
    public static final String SET_WARNED = "UPDATE "+table_prefix+"warnings SET warned=? WHERE id=?";

    public static final String ADD_HISTORY = "INSERT INTO "+table_prefix+"history (uuid, ip, name) SELECT ?,?,? FROM DUAL WHERE NOT EXISTS (SELECT * FROM litebans_history WHERE uuid=? AND ip=? AND name=?);";
    public static final String RESOLVE_NAME = "SELECT uuid FROM "+table_prefix+"history WHERE name=? ORDER BY 'time' DESC LIMIT 1;";
    public static final String RESOLVE_UUID = "SELECT name FROM "+table_prefix+"history WHERE uuid=? ORDER BY 'time' DESC LIMIT 1;";
    public static final String RESOLVE_UUID_TO_IP = "SELECT ip FROM "+table_prefix+"history WHERE uuid=? ORDER BY 'time' DESC LIMIT 1;";
    //I have no clue what I'm doing anymore.
    public static final String FIND_ALL_ALTS = "WITH RECURSIVE paths (ip, uuid_dest, name) AS (SELECT ip, uuid, name FROM litebans_history WHERE uuid=? UNION SELECT litebans_history.ip, litebans_history.uuid, litebans_history.name FROM paths JOIN litebans_history ON (paths.ip = litebans_history.ip or paths.uuid_dest = litebans_history.uuid))SELECT * FROM paths;";
    public static final String FIND_ALL_ALTS_BANNED = "WITH RECURSIVE paths (ip, uuid_dest, name) AS (SELECT ip, uuid, name FROM litebans_history WHERE uuid=? UNION SELECT litebans_history.ip, litebans_history.uuid, litebans_history.name FROM paths JOIN litebans_history ON (paths.ip = litebans_history.ip or paths.uuid_dest = litebans_history.uuid))SELECT *, false as warned FROM litebans_bans WHERE uuid in (select uuid_dest from paths) AND active=true AND (until>=(UNIX_TIMESTAMP()) OR until=-1);";

    public static Map<String,Ban> muteCache = new HashMap<>();

    private static HikariDataSource ds;

    public SQLStore(String jdbcURL, String username, String password) {
        HikariConfig config = new HikariConfig();

        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setJdbcUrl(jdbcURL);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);

        init();
    }

    private void init() {

        String[] createStrings = {CREATE_BANS,CREATE_MUTES,CREATE_WARNS,CREATE_KICKS, CREATE_HISTORY, CREATE_CONFIG, CREATE_VERSION};

        try {
            Connection conn = ds.getConnection();
            for(String str : createStrings) {
                Statement statement = conn.createStatement();
                statement.execute(str);
                statement.close();
            }
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private HashSet<Ban> runBanStatement(String stat,String uuid) {
        try(Connection conn = ds.getConnection()){
            PreparedStatement statement = conn.prepareStatement(stat);
            statement.setString(1,uuid);
            ResultSet res = statement.executeQuery();
            HashSet<Ban> bans = new HashSet<>();
            while (res.next()) {
                bans.add(new Ban(UUID.fromString(res.getString("uuid") != null ? res.getString("uuid") : "00000000-0000-0000-0000-000000000000"),res.getString("ip"),res.getString("reason"),UUID.fromString(res.getString("banned_by_uuid")),res.getString("server_scope"),res.getString("banned_by_name"),UUID.fromString(res.getString("removed_by_uuid") != null ? res.getString("removed_by_uuid") : "00000000-0000-0000-0000-000000000000"),res.getString("removed_by_name"),res.getString("server_origin"),res.getDouble("time"),res.getDouble("until"),res.getBoolean("silent"),res.getBoolean("ipban"),res.getBoolean("active"),res.getBoolean("warned"),res.getInt("id"),res.getDate("removed_by_date")));
            }
            conn.close();
            return bans;
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HashSet<Ban> getHistoricalBans(UUID uuid) {
        return runBanStatement(GET_BANS_BY_UUID_HISTORICAL, uuid.toString());
    }

    @Override
    public HashSet<Ban> getHistoricalMutes(UUID uuid) {
        return runBanStatement(GET_MUTES_BY_UUID_HISTORICAL, uuid.toString());
    }

    @Override
    public HashSet<Ban> getHistoricalWarns(UUID uuid) {
        return runBanStatement(GET_WARNS_BY_UUID_HISTORICAL, uuid.toString());
    }

    @Override
    public HashSet<Ban> getHistoricalKicks(UUID uuid) {
        return runBanStatement(GET_KICKS_BY_UUID_HISTORICAL, uuid.toString());
    }

    @Override
    public HashSet<Ban> getActiveBans(String uuid, String node, boolean ip) {
        if (ip) {
            return runBanStatement(GET_SOMETHING_BY_IP_ACTIVE.replace("%node%",node), uuid);
        }
        return runBanStatement(GET_SOMETHING_BY_UUID_ACTIVE.replace("%node%",node), uuid);
    }

    @Override
    public HashSet<Ban> getActiveBans(String uuid, boolean ip) {
        if (ip) {
            return runBanStatement(GET_SOMETHING_BY_IP_ACTIVE.replace("%node%","ban"), uuid);
        }
        return runBanStatement(GET_BANS_BY_UUID_ACTIVE, uuid);
    }

    @Override
    public HashSet<Ban> getActiveMutes(UUID uuid) {
        return null;
    }

    @Override
    public HashSet<Ban> getActiveWarns(String uuid) {
        if (uuid.contains(".") || uuid.contains(":")) {
            return runBanStatement(GET_SOMETHING_BY_IP_ACTIVE.replace("%node%","warn"), uuid);
        }
        return runBanStatement(GET_SOMETHING_BY_UUID_ACTIVE.replace("%node%","warn"), uuid);
    }


    @Override
    public void createServerIfNotExists(UUID uuid, String name) {

    }

    @Override
    public void getServer(String name) {

    }

    @Override
    public void addHistory(String name, UUID uuid, String ip) {
        try(Connection conn = ds.getConnection()){
            PreparedStatement statement = conn.prepareStatement(ADD_HISTORY);
            statement.setString(1,uuid.toString());
            statement.setString(2,ip);
            statement.setString(3,name);
            statement.setString(4,uuid.toString());
            statement.setString(5,ip);
            statement.setString(6,name);
            statement.execute();
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HashSet<Alt> getAlts(UUID uuid) {
        try(Connection conn = ds.getConnection()){
            PreparedStatement statement = conn.prepareStatement(FIND_ALL_ALTS);
            statement.setString(1,uuid.toString());
            ResultSet res = statement.executeQuery();
            HashSet<Alt> alts = new HashSet<>();
            while (res.next()) {
                alts.add(new Alt(res.getString("name"),UUID.fromString(res.getString("uuid_dest")),res.getString("ip")));
            }
            return alts;
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HashSet<Ban> getAltsBanned(UUID uuid) {
        return runBanStatement(FIND_ALL_ALTS_BANNED,uuid.toString());
    }

    @Override
    public String resolveUUID(UUID uuid) {
        try(Connection conn = ds.getConnection()){
            PreparedStatement statement = conn.prepareStatement(RESOLVE_UUID);
            statement.setString(1,uuid.toString());
            ResultSet res = statement.executeQuery();
            if (!res.first()) {
                return null;
            }
            return res.getString("name");
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String resolveUUIDToIp(UUID uuid) {
        try(Connection conn = ds.getConnection()){
            PreparedStatement statement = conn.prepareStatement(RESOLVE_UUID_TO_IP);
            statement.setString(1,uuid.toString());
            ResultSet res = statement.executeQuery();
            if (!res.first()) {
                return null;
            }
            return res.getString("ip");
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UUID resolveName(String name) {
        try(Connection conn = ds.getConnection()){
            PreparedStatement statement = conn.prepareStatement(RESOLVE_NAME);
            statement.setString(1,name);
            ResultSet res = statement.executeQuery();
            if (!res.first()) {
                return null;
            }
            return UUID.fromString(res.getString("uuid"));
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addBan(Ban ban) {
        try(Connection conn = ds.getConnection()){
            PreparedStatement statement = conn.prepareStatement(ADD_BAN);
            statement.setString(1,ban.getUuid()!=null?ban.getUuid().toString():null);
            statement.setString(2,ban.getIP());
            statement.setString(3,ban.getReason());
            statement.setString(4,ban.getBannedByUUID().toString());
            statement.setString(5,ban.getBannedByName());
            statement.setDouble(6,ban.getTime());
            statement.setDouble(7,ban.getUntil());
            statement.setBoolean(8,ban.isSilent());
            statement.setBoolean(9,ban.isIpban());
            statement.execute();
        }catch(SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void removeBan(Ban ban) {
        try(Connection conn = ds.getConnection()){
            PreparedStatement statement = conn.prepareStatement(REMOVE_BAN);
            statement.setInt(3,ban.getId());
            statement.setString(2,ban.getRemovedByName());
            statement.setString(1,ban.getRemovedByUUID().toString());
            statement.execute();
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Ban getBan(String uuid) {
        Set<Ban> bans;
        if (uuid.contains(".") || uuid.contains(":")) {
            bans = runBanStatement(GET_SOMETHING_BY_IP_ACTIVE.replace("%node%", "ban"), uuid);
        } else {
            bans = runBanStatement(GET_SOMETHING_BY_UUID_ACTIVE.replace("%node%", "ban"), uuid);
        }
        if (bans == null || bans.size()<1) return null;
        return (Ban) bans.toArray()[0];
    }

    @Override
    public Ban getMute(String uuid) {
        if (muteCache.containsKey(uuid))
            return muteCache.get(uuid);

        if (uuid.contains(".") || uuid.contains(":")) {
            Set<Ban> bans = runBanStatement(GET_SOMETHING_BY_IP_ACTIVE.replace("%node%","mute"),uuid);
            if (bans == null || bans.size()<1) return null;
            Ban ban = (Ban) bans.toArray()[0];
            muteCache.put(uuid,ban);
            return ban;
        } else {
            Set<Ban> bans = runBanStatement(GET_SOMETHING_BY_UUID_ACTIVE.replace("%node%","mute"),uuid);
            if (bans == null || bans.size()<1) return null;
            Ban ban = (Ban) bans.toArray()[0];
            muteCache.put(uuid,ban);
            return ban;
        }
    }

    @Override
    public void addWarn(Ban ban) {
        try(Connection conn = ds.getConnection()){
            PreparedStatement statement = conn.prepareStatement(ADD_WARN);
            statement.setString(1,ban.getUuid()!=null?ban.getUuid().toString():null);
            statement.setString(2,ban.getIP());
            statement.setString(3,ban.getReason());
            statement.setString(4,ban.getBannedByUUID().toString());
            statement.setString(5,ban.getBannedByName());
            statement.setDouble(6,ban.getTime());
            statement.setDouble(7,ban.getUntil());
            statement.setBoolean(8,ban.isSilent());
            statement.setBoolean(9,ban.isIpban());
            statement.execute();
        }catch(SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Ban getActiveUnwarned(UUID uuid) {
        Set<Ban> bans = runBanStatement(GET_ACTIVE_UNWARNED, uuid.toString());
        if (bans == null || bans.size()<1) return null;
        return (Ban) bans.toArray()[0];
    }

    @Override
    public void setWarned(Ban ban) {
        try(Connection conn = ds.getConnection()){
            PreparedStatement statement = conn.prepareStatement(SET_WARNED);
            statement.setBoolean(1,ban.isWarned());
            statement.setInt(2,ban.getId());
            statement.execute();

            muteCache.put(ban.getUuid().toString(), ban);
        }catch(SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void addMute(Ban ban) {
        try(Connection conn = ds.getConnection()){
            PreparedStatement statement = conn.prepareStatement(ADD_MUTE);
            statement.setString(1,ban.getUuid().toString());
            statement.setString(2,ban.getIP());
            statement.setString(3,ban.getReason());
            statement.setString(4,ban.getBannedByUUID().toString());
            statement.setString(5,ban.getBannedByName());
            statement.setDouble(6,ban.getTime());
            statement.setDouble(7,ban.getUntil());
            statement.setBoolean(8,ban.isSilent());
            statement.setBoolean(9,ban.isIpban());
            statement.execute();

            muteCache.put(ban.getUuid().toString(), ban);
        }catch(SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void removeMute(Ban ban) {
        try(Connection conn = ds.getConnection()){
            PreparedStatement statement = conn.prepareStatement(REMOVE_MUTE);
            statement.setInt(3,ban.getId());
            statement.setString(2,ban.getRemovedByName());
            statement.setString(1,ban.getRemovedByUUID().toString());
            statement.execute();

            ban.setActive(false);
            muteCache = new HashMap<>();
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addKick(Ban ban) {
        try(Connection conn = ds.getConnection()){
            PreparedStatement statement = conn.prepareStatement(ADD_KICK);
            statement.setString(1,ban.getUuid()!=null?ban.getUuid().toString():null);
            statement.setString(2,ban.getIP());
            statement.setString(3,ban.getReason());
            statement.setString(4,ban.getBannedByUUID().toString());
            statement.setString(5,ban.getBannedByName());
            statement.setDouble(6,ban.getTime());
            statement.setDouble(7,ban.getUntil());
            statement.setBoolean(8,ban.isSilent());
            statement.setBoolean(9,ban.isIpban());
            statement.execute();
        }catch(SQLException e) {
            e.printStackTrace();
        }

    }
}
