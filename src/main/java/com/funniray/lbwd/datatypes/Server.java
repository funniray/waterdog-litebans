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

import jline.internal.Nullable;

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
