package fr.mrlaikz.spartatreasure.tasks;

import fr.mrlaikz.spartatreasure.SpartaTreasure;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTimer extends BukkitRunnable {

    private SpartaTreasure plugin;
    private FileConfiguration config;

    private int timer;

    public GameTimer(SpartaTreasure plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        timer = config.getInt("times.game");
    }


    @Override
    public void run() {

        if(config.getIntegerList("broadcast.times").contains(timer)) {
            Bukkit.broadcastMessage(plugin.strConfig("broadcast.event_stop_in").replace("%time%", String.valueOf(timer)));
        }

        if(timer == 0) {
            plugin.getManager().stopEvent();
            cancel();
        }
        timer--;
    }
}
