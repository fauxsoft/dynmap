package org.dynmap;

public interface StaleQueue {

	public abstract int size();

	/* put a MapTile that needs to be regenerated on the list of stale tiles */
	public abstract boolean pushStaleTile(MapTile m);

	/*
	 * get next MapTile that needs to be regenerated, or null the mapTile is
	 * removed from the list of stale tiles!
	 */
	public abstract MapTile popStaleTile();

	public abstract boolean removeStaleTile(MapTile m);
	
	public abstract void clear();
}