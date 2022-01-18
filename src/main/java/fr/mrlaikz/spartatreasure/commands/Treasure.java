package fr.mrlaikz.spartatreasure.commands;

import fr.mrlaikz.spartatreasure.GameState;
import fr.mrlaikz.spartatreasure.SpartaTreasure;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class Treasure implements CommandExecutor {

    private SpartaTreasure plugin;
    private FileConfiguration config;

    public Treasure(SpartaTreasure plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        if(sender instanceof Player) {

            Player p = (Player) sender;
            if(p.hasPermission("spartatreasure.manage")) {
                if(args.length == 1) {

                    if(args[0].equalsIgnoreCase("start")) {
                        if (plugin.getManager().getState().equals(GameState.NONE)) {
                            plugin.getManager().waitEvent();
                        } else {
                            p.sendMessage(plugin.strConfig("message.already_event"));
                        }
                    } else if(args[0].equalsIgnoreCase("reload")) {
                        plugin.reloadConfig();
                        plugin.getManager().loadAllLocations();
                        p.sendMessage(plugin.strConfig("message.config_reloaded"));
                    } else if(args[0].equalsIgnoreCase("get")) {
                        if(plugin.getManager().getState().equals(GameState.GAME)) {
                            p.sendMessage("§cLocalisations des coffres:");
                            Location un = plugin.getManager().getLocations().get(0);
                            Location deux = plugin.getManager().getLocations().get(1);
                            Location trois = plugin.getManager().getLocations().get(2);
                            p.sendMessage("§61: " + un.getX() + " ; " + un.getY() + " ; " + un.getZ());
                            p.sendMessage("§62: " + deux.getX() + " ; " + deux.getY() + " ; " + deux.getZ());
                            p.sendMessage("§63: " + trois.getX() + " ; " + trois.getY() + " ; " + trois.getZ());
                        }
                    } else {
                        p.sendMessage("§cCommande Inconnue");
                    }
                }

                if(args.length == 2) {

                    if(args[0].equalsIgnoreCase("setloc")) {
                        Location loc = p.getLocation();
                        double x = loc.getX();
                        double y = loc.getY();
                        double z = loc.getZ();

                        config.set("locations." + args[1].toLowerCase(Locale.ROOT) + ".x", x);
                        config.set("locations." + args[1].toLowerCase(Locale.ROOT) + ".y", y);
                        config.set("locations." + args[1].toLowerCase(Locale.ROOT) + ".z", z);
                        plugin.saveConfig();
                        p.sendMessage(plugin.strConfig("message.config_updated"));
                    }

                    if(args[0].equalsIgnoreCase("setinv")) {
                        if(config.getConfigurationSection("inventories." + args[1].toLowerCase(Locale.ROOT)) == null) {
                            Block b = p.getTargetBlock(null, 50);
                            if (b.getType().equals(Material.CHEST)) {
                                Chest c = (Chest) b.getState();
                                if (!c.getBlockInventory().isEmpty()) {
                                    Inventory inv = c.getBlockInventory();
                                    int i = 0;
                                    for (ItemStack it : inv.getContents()) {
                                        if (it != null) {
                                            config.set("inventories." + args[1].toLowerCase(Locale.ROOT) + "." + i, it);
                                            i++;
                                        }
                                    }
                                    plugin.saveConfig();
                                    p.sendMessage(plugin.strConfig("message.chest_added"));
                                } else {
                                    p.sendMessage(plugin.strConfig("message.inv_empty"));
                                }
                            } else {
                                p.sendMessage(plugin.strConfig("message.error_chest"));
                            }
                        } else {
                            p.sendMessage(plugin.strConfig("message.error_inv"));
                        }
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
