package org.dynmap;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class PlayerLocPolling extends Thread {
    static final Logger log = Logger.getLogger("Minecraft");

    final Configuration configuration;
    final Server server;
    final MapManager mgr;

    public PlayerLocPolling(Configuration configuration, Server server, MapManager mapManager) {
        this.setName(PlayerLocPolling.class.getSimpleName());
        this.setPriority(MIN_PRIORITY);
        this.setDaemon(true);

        this.configuration = configuration;
        this.server = server;
        this.mgr = mapManager;
    }

    volatile boolean running;

    /**
     * Stops this thread. Operation will block until completed.
     */
    @Override
    public void destroy() {
        running = false;
        log.info(PlayerLocPolling.class.getSimpleName() + ": shutting down...");
    }

    @Override
    public void run() {
        running = true;
        int playerPollingWait = configuration.getInt("playerpollingwait", 30000);
        while (running) {
            if (mgr.fullRenderInProgress.get()) {
                try {
                    Thread.sleep(playerPollingWait * 4);
                } catch (InterruptedException e) {
                }
                log.info(Thread.currentThread().getName() + ": full render in progress. sleeping");
                continue;
            }

            Player[] players = server.getOnlinePlayers();

            if (players.length > 0)
                log.info(Thread.currentThread().getName() + ": polling player locations");

            for (Player p : players) {
                Location pLoc = p.getLocation();
                mgr.touch(pLoc.getBlockX(), pLoc.getBlockY(), pLoc.getBlockZ());
            }
            
            try {
                Thread.sleep(playerPollingWait);
            } catch (InterruptedException e) {
            }

        }
    }
}