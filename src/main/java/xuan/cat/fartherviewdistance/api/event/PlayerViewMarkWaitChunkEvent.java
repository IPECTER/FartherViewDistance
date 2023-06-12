package xuan.cat.fartherviewdistance.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import xuan.cat.fartherviewdistance.api.data.PlayerView;

public final class PlayerViewMarkWaitChunkEvent extends ExtendChunkEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final int chunkX;
    private final int chunkZ;
    private boolean cancel = false;


    public PlayerViewMarkWaitChunkEvent(PlayerView view, int chunkX, int chunkZ) {
        super(view);
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
