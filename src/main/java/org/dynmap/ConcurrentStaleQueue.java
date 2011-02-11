package org.dynmap;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class ConcurrentStaleQueue implements StaleQueue {
    protected static final Logger log = Logger.getLogger("Minecraft");

    /* a list of MapTiles to be updated */
    private ConcurrentLinkedQueue<MapTile> staleTilesQueue;

    // rough count
    private AtomicInteger count = new AtomicInteger();

    public ConcurrentStaleQueue() {
        staleTilesQueue = new ConcurrentLinkedQueue<MapTile>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dynmap.IStaleQueue#size()
     */
    public int size() {
        return count.get();
    }

    /* put a MapTile that needs to be regenerated on the list of stale tiles */
    /*
     * (non-Javadoc)
     * 
     * @see org.dynmap.IStaleQueue#pushStaleTile(org.dynmap.MapTile)
     */
    public boolean pushStaleTile(MapTile m) {
        if (!staleTilesQueue.contains(m)) {
            int size = count.incrementAndGet();
            staleTilesQueue.offer(m);
            log.fine(Thread.currentThread().getName() + ": pushed stale tile: renderQueue.size=" + size);
            return true;
        } else {
            log.fine(Thread.currentThread().getName() + ": skipped already stale tile");
        }
        return false;
    }

    /*
     * get next MapTile that needs to be regenerated, or null the mapTile is
     * removed from the list of stale tiles!
     */
    /*
     * (non-Javadoc)
     * 
     * @see org.dynmap.IStaleQueue#popStaleTile()
     */
    public MapTile popStaleTile() {
        MapTile tile = staleTilesQueue.poll();
        if (tile != null) {
            int size = count.decrementAndGet();
            log.fine(Thread.currentThread().getName() + ": popped stale tile: renderQueue.size=" + size);
        } else {
            log.fine(Thread.currentThread().getName() + ": staleTilesQueue.poll() empty queue");
        }
        return tile;
    }

    @Override
    public boolean removeStaleTile(MapTile m) {
        return staleTilesQueue.remove(m);
    }

    @Override
    public void clear() {
        staleTilesQueue.clear();
    }
}
