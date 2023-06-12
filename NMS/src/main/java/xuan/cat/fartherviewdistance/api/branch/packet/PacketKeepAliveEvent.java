package xuan.cat.fartherviewdistance.api.branch.packet;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public final class PacketKeepAliveEvent extends PacketEvent {
    private static final HandlerList handlers = new HandlerList();
    private final long id;

    public PacketKeepAliveEvent(Player player, long id) {
        super(player);
        this.id = id;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public long getId() {
        return id;
    }
}