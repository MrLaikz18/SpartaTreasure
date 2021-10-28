package fr.mrlaikz.spartatreasure.tasks;

import fr.mrlaikz.spartatreasure.GameState;
import fr.mrlaikz.spartatreasure.SpartaTreasure;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class ChestTimer extends BukkitRunnable {

    private SpartaTreasure plugin;
    private FileConfiguration config;

    private int timer;
    private Location loc;
    private Boolean last;

    public ChestTimer(SpartaTreasure plugin, Location loc, Boolean last) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.loc = loc;
        this.last = last;
        timer = config.getInt("times.chest_delete");
    }

    @Override
    public void run() {
        if(timer == 0 ) {
            loc.getBlock().setType(Material.AIR);
            if(Boolean.TRUE.equals(last) && plugin.getManager().getState().equals(GameState.GAME)) {
                plugin.getManager().stopGameTimer();
                plugin.getManager().stopEvent();
            }
            cancel();
        }
        timer--;
    }
}
