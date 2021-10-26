package fr.mrlaikz.spartatreasure.database;

import fr.mrlaikz.spartatreasure.SpartaTreasure;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Data {

    private SpartaTreasure plugin;
    private FileConfiguration config;

    private String table;

    public Data(SpartaTreasure plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        table = config.getString("database.table");
    }

    public void createTable() {
        try {
            PreparedStatement ps = plugin.getDatabase().prepareStatement("CREATE TABLE IF NOT EXISTS " + table + "(id INT AUTO_INCREMENT PRIMARY KEY," +
                    " player VARCHAR(255), points INT(11))");
            ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(String nom) {
        try {
            PreparedStatement ps = plugin.getDatabase().prepareStatement("SELECT * FROM " + table + " WHERE player = ?");
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return true;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void insertPlayer(String nom) {
        try {
            PreparedStatement ps = plugin.getDatabase().prepareStatement("INSERT INTO " + table + " (player, points) VALUES(?, 0)");
            ps.setString(1, nom);
            ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPoints(String nom) {
        try {
            PreparedStatement ps = plugin.getDatabase().prepareStatement("SELECT * FROM " + table + " WHERE player = ?");
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt("points");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addPoints(String nom) {
        if(playerExists(nom)) {
            try {
                PreparedStatement ps = plugin.getDatabase().prepareStatement("UPDATE " + table + " SET points = ? WHERE player = ?");
                ps.setInt(1, getPoints(nom)+1);
                ps.setString(2, nom);
                ps.executeUpdate();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else {
            insertPlayer(nom);
        }
    }

    public ArrayList<String> top() {
        ArrayList<String> ret = new ArrayList<String>();
        try {
            PreparedStatement ps = plugin.getDatabase().prepareStatement("SELECT player FROM " + table + " ORDER BY points DESC LIMIT 10");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                ret.add(rs.getString("player"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

}
