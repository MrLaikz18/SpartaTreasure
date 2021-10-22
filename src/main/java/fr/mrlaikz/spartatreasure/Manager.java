package fr.mrlaikz.spartatreasure;

import fr.mrlaikz.spartatreasure.tasks.GameTimer;
import fr.mrlaikz.spartatreasure.tasks.WaitTimer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Random;

public class Manager {

    private SpartaTreasure plugin;
    private FileConfiguration config;

    public Manager(SpartaTreasure plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    //EVENT
    private GameState state;
    private int round;
    private int opened;

    //GAME
    private ArrayList<Location> allLocations = new ArrayList<Location>();
    private ArrayList<Inventory> allInventories = new ArrayList<Inventory>();
    private ArrayList<Location> savedLocations = new ArrayList<Location>();

    //EVENT
    public GameState getState() {
        return state;
    }

    public int getRound() {
        return round;
    }

    public void addOpenedChests() {
        opened++;
    }

    public int getOpenedChests() {
        return opened;
    }

    public void waitEvent() {
        state = GameState.WAITING;
        Bukkit.broadcastMessage(plugin.strConfig("broadcast.event_wait"));
        WaitTimer timer = new WaitTimer(plugin);
        timer.runTaskTimer(plugin, 0, 20);
    }

    public void startEvent() {
        round++;
        spawnChests();
        Bukkit.broadcastMessage(plugin.strConfig("broadcast.event_start"));
        GameTimer timer = new GameTimer(plugin);
        timer.runTaskTimer(plugin, 0, 20);
    }

    public void stopEvent() {
        for(Location loc : allLocations) {
            loc.getBlock().setType(Material.AIR);
        }
        round = 0;
        state = GameState.NONE;
    }

    //GAME
    public ArrayList<Location> getLocations() {
        return savedLocations;
    }

    public ArrayList<Location> getAllLocations() {
        return allLocations;
    }

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
                Location add = new Location(Bukkit.getWorld(config.getString("world")), x, y, z);
                allLocations.add(add);
            }
        }
    }

    public void loadAllInventories() {
        //LOAD allInventories
        allInventories.clear();
        for (String invent : config.getConfigurationSection("inventories").getKeys(false)) {
            Inventory inv = Bukkit.createInventory(null, 27);
            for (String item : config.getConfigurationSection("inventories." + invent).getKeys(false)) {

                ItemStack it = new ItemStack(Material.matchMaterial(item), config.getInt("inventories." + invent + "." + item + ".amount"));
                ItemMeta itM = it.getItemMeta();

                if (plugin.getConfig().getConfigurationSection("inventories." + invent + "." + item + ".enchants") != null) {
                    for (String enchant : plugin.getConfig().getConfigurationSection("inventories." + invent + "." + item + ".enchants").getKeys(false)) {
                        Integer enchantlvl = config.getInt("inventories." + invent + "." + item + ".enchants." + enchant);
                        itM.addEnchant(Enchantment.getByKey(NamespacedKey.minecraft(enchant.toLowerCase())), enchantlvl, true);
                    }
                }

                it.setItemMeta(itM);
                inv.addItem(it);
            }
            allInventories.add(inv);
        }
    }

    public void loadLocations() {
        //SAVE 3 RANDOM LOCATIONS
        savedLocations.clear();
        Random r = new Random();
        if(allLocations.size() == 3) {
            savedLocations = allLocations;
        } else {
            ArrayList<Location> locations = allLocations;
            while(savedLocations.size() != 3) {
                int rd = r.nextInt(locations.size());
                savedLocations.add(locations.get(rd));
                locations.remove(rd);
            }
        }
    }

    public void spawnChests() {
        Random r = new Random();
        //SAVE 3 RANDOM LOCATIONS
        loadLocations();

        //SAVE 3 RANDOM INVENTORIES
        ArrayList<Inventory> savedInventories = new ArrayList<Inventory>();
        if(allInventories.size() == 3) {
            savedInventories = allInventories;
        } else {
            ArrayList<Inventory> inventories = allInventories;
            while(savedInventories.size() != 3) {
                int rd = r.nextInt(inventories.size());
                savedInventories.add(inventories.get(rd));
                inventories.remove(rd);
            }
        }

        //SPAWN CHESTS
        for(Location loc : savedLocations) {
            loc.getBlock().setType(Material.CHEST);
            Chest c = (Chest) loc.getBlock().getState();
            Inventory invChest = savedInventories.get(savedInventories.size()-1);
            for(ItemStack it : invChest.getContents()) {
                c.getBlockInventory().setItem(r.nextInt(26), it);
            }
            savedInventories.remove(savedInventories.size()-1);
        }

    }



}
