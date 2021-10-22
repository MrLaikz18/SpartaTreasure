package fr.mrlaikz.spartatreasure.listener;

import fr.mrlaikz.spartatreasure.GameState;
import fr.mrlaikz.spartatreasure.SpartaTreasure;
import fr.mrlaikz.spartatreasure.database.Data;
import fr.mrlaikz.spartatreasure.tasks.ChestTimer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChestOpen implements Listener {

    private SpartaTreasure plugin;
    private FileConfiguration config;
    private Data data;

    public ChestOpen(SpartaTreasure plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void onOpenChest(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        Location loc = b.getLocation();

        if(plugin.getManager().getState().equals(GameState.GAME)) {
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if(b != null && b.getType().equals(Material.CHEST)) {
                    if(plugin.getManager().getLocations().contains(loc)) {
                        plugin.getManager().addOpenedChests();
                        plugin.getManager().getLocations().remove(loc);
                        data.addPoints(p.getName());
                        Bukkit.broadcastMessage(plugin.strConfig("broadcast.chest_opened")
                                .replace("%player%", p.getName()));
                        ChestTimer timer = new ChestTimer(plugin, loc);
                        timer.runTaskTimer(plugin, 0, 20);
                    }
                }
            }
        }
    }
}
