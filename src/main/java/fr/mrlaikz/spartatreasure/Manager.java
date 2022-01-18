package fr.mrlaikz.spartatreasure;

import fr.mrlaikz.spartatreasure.tasks.GameTimer;
import fr.mrlaikz.spartatreasure.tasks.WaitTimer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class Manager {

    private SpartaTreasure plugin;
    private FileConfiguration config;

    public Manager(SpartaTreasure plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        state = GameState.NONE;
    }

    //EVENT
    private GameState state;
    private int opened = 0;

    //GAME
    private ArrayList<Location> allLocations = new ArrayList<Location>();
    private ArrayList<Inventory> allInventories = new ArrayList<Inventory>();
    private ArrayList<Location> savedLocations = new ArrayList<Location>();

    private WaitTimer waitTimer;
    private GameTimer gameTimer;

    //EVENT
    public GameState getState() {
        return state;
    }

    public void addOpenedChests() {
        opened++;
    }

    public int getOpenedChests() {
        return opened;
    }

    public void waitEvent() {
        state = GameState.WAITING;
        waitTimer = new WaitTimer(plugin);
        waitTimer.runTaskTimer(plugin, 0, 20);
        Bukkit.broadcastMessage(plugin.strConfig("broadcast.event_wait"));
    }

    public void startEvent() {
        state = GameState.GAME;
        opened = 0;
        spawnChests();
        gameTimer = new GameTimer(plugin);
        gameTimer.runTaskTimer(plugin, 0, 20);
        Bukkit.broadcastMessage(plugin.strConfig("broadcast.event_start"));
    }

    public void stopGameTimer() {
        gameTimer.cancel();
    }

    public void stopEvent() {
        for(Location loc : allLocations) {
            loc.getBlock().setType(Material.AIR);
        }
        state = GameState.NONE;
        Bukkit.broadcastMessage(plugin.strConfig("broadcast.event_finished")
                .replace("%opened%", String.valueOf(getOpenedChests())));
    }

    public ArrayList<Location> getAllLocations() {
        return allLocations;
    }

    public ArrayList<Inventory> getAllInventories() { return allInventories; }

    public void load() {
        loadAllLocations();
        loadAllInventories();
    }

    public void loadAllLocations() {
        //LOAD allLocations
        allLocations.clear();
        if(config.getConfigurationSection("locations") != null) {
            for (String loc : config.getConfigurationSection("locations").getKeys(false)) {
                int x = config.getInt("locations." + loc + ".x");
                int y = config.getInt("locations." + loc + ".y");
                int z = config.getInt("locations." + loc + ".z");
                Location add = new Location(Bukkit.getWorld("spawn"), x, y, z);
                allLocations.add(add);
            }
        }
    }

    public void loadAllInventories() {
        //LOAD allInventories
        allInventories.clear();
        if(config.getConfigurationSection("inventories") != null) {
            for (String invent : config.getConfigurationSection("inventories").getKeys(false)) {
                Inventory inv = Bukkit.createInventory(null, 27);
                for (String item : config.getConfigurationSection("inventories." + invent).getKeys(false)) {
                    ItemStack it = config.getItemStack("inventories." + invent + "." + item);
                    inv.addItem(it);
                }
                allInventories.add(inv);
            }
        }
    }

    public ArrayList<Location> getLocations() {
        return savedLocations;
    }

    public void clearChests() {
        for(Location loc : allLocations) {
            loc.getBlock().setType(Material.AIR);
        }
    }

    public void spawnChests() {
        Random r = new Random();

        loadAllInventories();
        ArrayList<Inventory> savedInventories = new ArrayList<Inventory>();
        if(allInventories.size() == 3) {
            savedInventories = allInventories;
        } else {
            while(savedInventories.size() != 3) {
                int index = r.nextInt(allInventories.size());
                if(!savedInventories.contains(allInventories.get(index))) {
                    savedInventories.add(allInventories.get(index));
                }
            }
        }
        //SAVE 3 RANDOM LOCATIONS
        savedLocations.clear();
        if(allLocations.size() == 3) {
            savedLocations = allLocations;
        } else {
            while(savedLocations.size() != 3) {
                int index = r.nextInt(allLocations.size());
                if(!savedLocations.contains(allLocations.get(index))) {
                    savedLocations.add(allLocations.get(index));
                }
            }
        }

        //SPAWN CHESTS
        int t = 0;
        for(Location loc : savedLocations) {
            Inventory inv = savedInventories.get(t);
            loc.getBlock().setType(Material.CHEST);
            Chest c = (Chest) loc.getBlock().getState();
            for(ItemStack it : inv.getContents()) {
                if(it != null) {
                    c.getBlockInventory().setItem(r.nextInt(27), it);
                }
            }
            t++;
        }

    }

}
