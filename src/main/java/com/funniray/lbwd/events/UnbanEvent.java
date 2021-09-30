package com.funniray.lbwd.events;

import com.funniray.lbwd.datatypes.Ban;
import dev.waterdog.waterdogpe.event.CancellableEvent;
import dev.waterdog.waterdogpe.event.Event;

public class UnbanEvent extends Event implements CancellableEvent {
    private Ban ban;
    private String node;
    private boolean cancelled;

    public UnbanEvent(Ban ban, String node) {
        this.ban = ban;
        this.node = node;
    }

    public Ban getBan() {
        return this.ban;
    }

    public String getNode() {
        return node;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public void setCancelled() {
        this.cancelled = true;
    }
}
