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

package com.funniray.lbwd.datatypes;

import com.funniray.lbwd.LBWD;
import com.funniray.lbwd.events.BanEvent;
import com.funniray.lbwd.utils.Colors;
import com.funniray.lbwd.utils.DateUtils;
import com.funniray.lbwd.utils.UseQuotes;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

import javax.annotation.Nullable;
import java.util.*;

public class Ban {

    private int id;
    private UUID uuid;
    private String IP;
    private String reason;
    private UUID bannedByUUID;
    private String bannedByName;
    private UUID removedByUUID;
    private String removedByName;
    private Date removedByDate;
    private double time;
    private double until;
    private String serverScope;
    private String serverOrigin;
    private boolean silent;
    private boolean ipban;
    private boolean active;
    private boolean warned; //Warning only
    private String name; //Only internal use

    public Ban(@Nullable UUID uuid, @Nullable String ip, String reason, @Nullable UUID bannedByUUID, @Nullable String serverScope,
               @Nullable String bannedByName, @Nullable UUID removedByUUID, @Nullable String removedByName, @Nullable String serverOrigin,
               double time, double until, boolean silent, boolean ipban, boolean active, boolean warned, int id, Date removedByDate) {

        this.uuid = uuid;
        this.IP = ip;
        this.reason = reason;
        this.bannedByUUID = bannedByUUID;
        this.bannedByName = bannedByName;
        this.removedByUUID = removedByUUID;
        this.removedByName = removedByName;
        this.removedByDate = removedByDate;
        this.time = time;
        this.until = until;
        this.serverScope = serverScope;
        this.serverOrigin = serverOrigin;
        this.silent = silent;
        this.ipban = ipban;
        this.active = active;
        this.warned = warned;
        this.id = id;

    }

    //For banning a specific IP
    public Ban(String ip, String reason, @Nullable UUID bannedByUUID, @Nullable String bannedByName,
               double time, double until, boolean silent, boolean active, @Nullable String serverScope) {

        this.uuid = null;
        this.IP = ip;
        this.reason = reason;
        this.bannedByUUID = bannedByUUID;
        this.bannedByName = bannedByName;
        this.time = time;
        this.until = until;
        this.silent = silent;
        this.ipban = true;
        this.active = active;
        this.warned = false;

    }

    //For banning a user
    public Ban(UUID uuid, @Nullable String ip, String reason, @Nullable UUID bannedByUUID,
               @Nullable String bannedByName, double time, double until, boolean silent, boolean ipban,
               boolean active, @Nullable String serverScope, boolean warned) {

        this.uuid = uuid;
        this.IP = ip;
        this.reason = reason;
        this.bannedByUUID = bannedByUUID;
        this.bannedByName = bannedByName;
        this.time = time;
        this.until = until;
        this.silent = silent;
        this.ipban = ipban;
        this.active = active;
        this.warned = warned;

    }

    public static Ban createBan(CommandSender commandSender, String s, String[] strings) {
        strings = UseQuotes.parseArgs(strings);
        String playerName = strings[0];
        boolean silent = false;
        String reason;
        UUID uuid;
        long until = -1;

        String node = "ban";
        if (s.contains("mute")) node = "mute";
        if (s.contains("warn")) node = "warn";
        if (s.contains("kick")) node = "kick";

        int start = 1;

        if (strings[0].equals("-s")) {
            silent = true;
            playerName = strings[1];
            start++;
        }

        if (s.contains("temp")) {
            String timeString = strings[start];
            try {
                until = DateUtils.parseDateDiff(timeString,true);
                start++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!commandSender.hasPermission("litebans."+(until>-1?"temp":"")+node)) {
            commandSender.sendMessage(Colors.RED+"You do not have permission to "+(until>-1?"temp":"")+node+" people.");
            return null;
        }

        reason = String.join(" ", Arrays.copyOfRange(strings,start,strings.length));

        final ProxiedPlayer p = ProxyServer.getInstance().getPlayer(playerName);

        String senderName = commandSender.getName();
        UUID senderUuid;
        String ip = null;
        boolean isIpBan = s.contains("ip") || playerName.contains(".") || playerName.contains(":");

        if (isIpBan && !commandSender.hasPermission("litebans."+(until>-1?"temp":"")+"ip"+node)) {
            commandSender.sendMessage(Colors.RED+"You do not have permission to ip"+node+" people.");
            return null;
        }

        if (playerName.length()==36 && playerName.contains("-")) {
            uuid = UUID.fromString(playerName);
        } else if (p != null) {
            uuid = p.getUniqueId();
            ip = p.getAddress().getAddress().getHostAddress();
        } else if (isIpBan && (playerName.contains(".") || playerName.contains(":"))) {
            uuid = null;
            ip = playerName;
        } else {
            uuid = LBWD.datastore.resolveName(playerName);
            ip = LBWD.datastore.resolveUUIDToIp(uuid);
            if (uuid==null){
                if (isIpBan) {
                    ip = playerName;
                    uuid = null;
                } else {
                    commandSender.sendMessage(Colors.RED + "The player " + playerName + " has never joined the server.");
                    return null;
                }
            }
        }

        if (commandSender.isPlayer()) {
            senderUuid = ((ProxiedPlayer) commandSender).getUniqueId();
        } else {
            senderName = "Console";
            senderUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        }

        Set<Ban> bans = node.equals("kick")?new HashSet<>():LBWD.datastore.getActiveBans(isIpBan?ip:uuid.toString(),node,isIpBan);

        if (s.contains("un") || s.contains("pardon")) {
            if (bans.size() < 1) {
                commandSender.sendMessage(Colors.RED + "That player isn't "+node+"ned.");
                return null;
            }

            Ban ban = (Ban) bans.toArray()[0];
            if (ban.getBannedByUUID() != senderUuid && !commandSender.hasPermission("litebans.un"+node)) {
                commandSender.sendMessage(Colors.RED+"You can only un"+node+" players you've "+node+"ned.");
                return null;
            }

            ban.removedByName = senderName;
            ban.removedByUUID = senderUuid;

            return ban;
        }

        if (bans.size() > 0 && (node.equals("ban") || node.equals("mute"))) {
            commandSender.sendMessage(Colors.RED + "That player is already "+node+"ned with reason: "+((Ban) bans.toArray()[0]).getReason());
            return null;
        }

        if (p != null && (p.hasPermission("litebans.exempt") || p.hasPermission("litebans.exempt."+node))) {
            commandSender.sendMessage(Colors.RED + "That player is exempt.");
            return null;
        }

        Ban ban = new Ban(uuid,ip,reason,senderUuid,senderName,(double) (new Date()).getTime(),until,silent,isIpBan,true,"*",false);
        ban.name = playerName;

        BanEvent event = new BanEvent(ban,node);

        ProxyServer.getInstance().getEventManager().callEvent(event);

        if (event.isCancelled()) {
            return null;
        }

        return ban;
    }

    public String getName() {
        if (this.name == null)
            this.name = LBWD.datastore.resolveUUID(this.uuid);

        return this.name;
    }

    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getIP() {
        return IP;
    }

    public String getReason() {
        return reason;
    }

    public UUID getBannedByUUID() {
        return bannedByUUID;
    }

    public String getBannedByName() {
        return bannedByName;
    }

    public UUID getRemovedByUUID() {
        return removedByUUID;
    }

    public String getRemovedByName() {
        return removedByName;
    }

    public Date getRemovedByDate() {
        return removedByDate;
    }

    public double getTime() {
        return time;
    }

    public double getUntil() {
        return until;
    }

    public String getServerScope() {
        return serverScope;
    }

    public String getServerOrigin() {
        return serverOrigin;
    }

    public boolean isSilent() {
        return silent;
    }

    public boolean isIpban() {
        return ipban;
    }

    public boolean isActive() {
        boolean isExpired = (this.until < (new Date()).getTime());
        return this.active && (!isExpired || this.until == -1);
    }

    public String getUntilString() {
        return DateUtils.getDurationBreakdown((long) (this.getUntil() - (new Date()).getTime()));
    }

    public boolean isWarned() {
        return warned;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setWarned(boolean warned) {
        this.warned = warned;
    }
}
