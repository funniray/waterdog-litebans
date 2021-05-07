package com.funniray.lbwd.datatypes;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.UUID;

public class Server {

    private int id;
    private String name;
    private UUID uuid;
    private Date timestamp;

    public Server(@Nullable int id, String name, UUID uuid, @Nullable Date timestamp) {
        this.id = id;
        this.name = name;
        this.uuid = uuid;
        this.timestamp = timestamp;
    }
}
