package com.github.countercocoa.deliveryplugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class DeliveryPlugin extends JavaPlugin implements Listener {

    private Map<UUID, String> playerMap = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーのみが実行できます。");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "/del <送り先のプレイヤー名>");
            return true;
        }
        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "プレイヤーが存在しません。");
            return true;
        }
        if (targetPlayer.getName().equals(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "自分自身には送れません。");
            return true;
        }
        Inventory inventory = Bukkit.createInventory(null, InventoryType.CHEST, "Delivery Chest");
        ((Player) sender).openInventory(inventory);
        playerMap.put(((Player) sender).getUniqueId(), targetPlayer.getName());
        return true;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (playerMap.containsKey(uuid)) {
            String targetName = playerMap.get(uuid);
            playerMap.remove(uuid);
            Player targetPlayer = Bukkit.getPlayer(targetName);
            if (targetPlayer == null) {
                player.sendMessage(ChatColor.RED + "プレイヤーがオフラインです。");
                return;
            }
            ItemStack[] contents = event.getInventory().getContents();
            for (ItemStack item : contents) {
                if (item == null || item.getType() == Material.AIR) {
                    continue;
                }
                targetPlayer.getInventory().addItem(item);
            }
            targetPlayer.sendMessage(ChatColor.GREEN + "[Delivery]" + player.getName() + "から荷物が送られてきました！");
            player.sendMessage(ChatColor.GREEN + "[Derivery]荷物を送信しました。");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            player.sendMessage(ChatColor.GREEN + "おかえりなさい、" + player.getName() + "さん！/delで配達便をご利用いただけます。");
        } else {
            player.sendMessage(ChatColor.GREEN + "こんにちは、" + player.getName() + "さん！/delで配達便をご利用いただけます。");
        }
    }
}
