package fr.mrlaikz.spartatreasure.database;

import fr.mrlaikz.spartatreasure.SpartaTreasure;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private SpartaTreasure plugin;
    private FileConfiguration config;

    private String host;
    private String port;
    private String database;
    private String user;
    private String password;

    private Connection connection;

    public MySQL(SpartaTreasure plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        init();
    }

    private void init(){
        host = config.getString("database.host");
        port = config.getString("database.port");
        database = config.getString("database.database");
        user = config.getString("database.user");
        password = config.getString("database.password");
    }

    public boolean isConnected() {
        return !(this.connection == null);
    }

    public void connect() throws SQLException {
        if(!isConnected()) {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?useSSL=false", this.user, this.password);
        }
    }

    public void disconnect() {
        if (isConnected())
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public Connection getConnection() {
        return this.connection;
    }

}
