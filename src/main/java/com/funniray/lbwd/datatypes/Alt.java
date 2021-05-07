package com.funniray.lbwd.datatypes;

import java.util.UUID;

public class Alt {

    private String name;
    private UUID uuid;
    private String ip;


    public Alt(String name, UUID uuid, String ip) {
        this.name = name;
        this.uuid = uuid;
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getIp() {
        return ip;
    }
}
