package fr.mrlaikz.spartatreasure;

import fr.mrlaikz.spartatreasure.commands.Treasure;
import fr.mrlaikz.spartatreasure.listener.ChestOpen;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class SpartaTreasure extends JavaPlugin {

    private Manager manager;

    @Override
    public void onEnable() {
        //CONFIG
        saveDefaultConfig();
        manager = new Manager(this);
        manager.load();

        //COMMANDS
        getCommand("treasure").setExecutor(new Treasure(this));

        //EVENTS
        getServer().getPluginManager().registerEvents(new ChestOpen(this), this);

        //MISC
        getLogger().info("Plugin Actif");
    }

    public Manager getManager() {
        return manager;
    }

    public String strConfig(String path) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(path));
    }

}
