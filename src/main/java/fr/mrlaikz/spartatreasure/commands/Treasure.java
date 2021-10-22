package fr.mrlaikz.spartatreasure.commands;

import fr.mrlaikz.spartatreasure.GameState;
import fr.mrlaikz.spartatreasure.SpartaTreasure;
import fr.mrlaikz.spartatreasure.database.Data;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

public class Treasure implements CommandExecutor {

    private SpartaTreasure plugin;
    private FileConfiguration config;
    private Data data;

    public Treasure(SpartaTreasure plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        if(sender instanceof Player) {

            Player p = (Player) sender;

            if(args.length == 0) {
                ArrayList<String> top = data.top();
                for(String pl : top) {
                    p.sendMessage(plugin.strConfig("message.top")
                            .replace("%player%", pl)
                            .replace("%points%", String.valueOf(data.getPoints(pl))));
                }
            }

            if(p.hasPermission("spartatreasure.manage")) {
                if(args.length == 1) {

                    if(args[0].equalsIgnoreCase("start")) {
                        if (plugin.getManager().getState().equals(GameState.NONE)) {
                            plugin.getManager().waitEvent();
                        } else {
                            p.sendMessage(plugin.strConfig("message.already_event"));
                        }
                    }

                    if(args[0].equalsIgnoreCase("reload")) {
                        plugin.reloadConfig();
                        plugin.getManager().load();
                        p.sendMessage(plugin.strConfig("message.config_reloaded"));
                    }

                }

                if(args.length == 2) {

                    if(args[0].equalsIgnoreCase("setloc")) {
                        Location loc = p.getLocation();
                        int x = (int) loc.getX();
                        int y = (int) loc.getY();
                        int z = (int) loc.getZ();

                        config.set("locations." + args[1].toLowerCase(Locale.ROOT) + ".x", x);
                        config.set("locations." + args[1].toLowerCase(Locale.ROOT) + ".y", y);
                        config.set("locations." + args[1].toLowerCase(Locale.ROOT) + ".z", z);
                        plugin.saveConfig();
                        p.sendMessage(plugin.strConfig("message.config_updated"));
                    }

                }

            } else {
                p.sendMessage(plugin.strConfig("message.permission"));
            }

        } else {
            if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                plugin.reloadConfig();
                sender.sendMessage("CONFIGURATION RELOADED");
            } else {
                sender.sendMessage("COMMANDE INACCESSIBLE DEPUIS LA CONSOLE");
            }
        }

        return false;
    }
}
