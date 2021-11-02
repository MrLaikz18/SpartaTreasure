package fr.mrlaikz.spartatreasure;

import fr.mrlaikz.spartatreasure.commands.Treasure;
import fr.mrlaikz.spartatreasure.database.Data;
import fr.mrlaikz.spartatreasure.database.MySQL;
import fr.mrlaikz.spartatreasure.listener.ChestOpen;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class SpartaTreasure extends JavaPlugin {

    private Manager manager;
    private MySQL sql;
    private Data db;

    @Override
    public void onEnable() {
        //CONFIG
        saveDefaultConfig();
        manager = new Manager(this);
        manager.load();

        //COMMANDS
        getCommand("treasure").setExecutor(new Treasure(this));

        //DATABASE
        sql = new MySQL(this);
        db = new Data(this);
        try {
            sql.connect();
            db.createTable();
        } catch(SQLException e) {
            e.printStackTrace();
            getLogger().log(Level.SEVERE, "Connexion a la base de donnée impossible");
        }

        //EVENTS
        getServer().getPluginManager().registerEvents(new ChestOpen(this), this);

        //MISC
        getLogger().info("Plugin Actif");
    }

    public Manager getManager() {
        return manager;
    }

    public Connection getDatabase() {
        return sql.getConnection();
    }

    public String strConfig(String path) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(path));
    }

}
