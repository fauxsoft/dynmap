package org.dynmap;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

public class SyncStaleQueue implements StaleQueue {
    /* a list of MapTiles to be updated */
    private LinkedList<MapTile> staleTilesQueue;
    private Set<MapTile> staleTiles;

    public SyncStaleQueue() {
        staleTilesQueue = new LinkedList<MapTile>();
        staleTiles = new HashSet<MapTile>();
    }

    /* (non-Javadoc)
	 * @see org.dynmap.IStaleQueue#size()
	 */
    public int size() {
        return staleTilesQueue.size();
    }

    /* put a MapTile that needs to be regenerated on the list of stale tiles */
    /* (non-Javadoc)
	 * @see org.dynmap.IStaleQueue#pushStaleTile(org.dynmap.MapTile)
	 */
    public boolean pushStaleTile(MapTile m) {
        synchronized (MapManager.lock) {
            if (staleTiles.add(m)) {
                staleTilesQueue.addLast(m);
                return true;
            }
            return false;
        }
    }

    /*
     * get next MapTile that needs to be regenerated, or null the mapTile is
     * removed from the list of stale tiles!
     */
    /* (non-Javadoc)
	 * @see org.dynmap.IStaleQueue#popStaleTile()
	 */
    public MapTile popStaleTile() {
        synchronized (MapManager.lock) {
            try {
                MapTile t = staleTilesQueue.removeFirst();
                if (!staleTiles.remove(t)) {
                    // This should never happen.
                }
                return t;
            } catch (NoSuchElementException e) {
                return null;
            }
        }
    }
}
