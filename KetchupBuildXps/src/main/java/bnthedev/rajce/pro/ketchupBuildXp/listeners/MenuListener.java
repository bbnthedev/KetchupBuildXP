package bnthedev.rajce.pro.ketchupBuildXp.listeners;

import bnthedev.rajce.pro.ketchupBuildXp.KetchupBuildXp;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class MenuListener implements Listener {

    private final KetchupBuildXp plugin;

    public MenuListener(KetchupBuildXp plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String menuTitle = plugin.getConfigManager().getMessage("gui.menu-title");

        if (event.getView().getTitle().equals(ChatColor.YELLOW + "XP Statistiky")) {
            event.setCancelled(true);

            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }

            if (clickedItem.getItemMeta() instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) clickedItem.getItemMeta();

                if (skullMeta.getOwningPlayer() != null) {
                    String clickedPlayerName = skullMeta.getOwningPlayer().getName();

                    showDetailedStats(player, clickedPlayerName);
                }
            }
        }
    }

    private void showDetailedStats(Player viewer, String targetPlayerName) {
        int xp = plugin.getXpManager().getXp(targetPlayerName);
        int position = plugin.getXpManager().getPlayerPosition(targetPlayerName);

        if (position == -1) {
            viewer.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
            return;
        }

        viewer.closeInventory();

        viewer.sendMessage(plugin.getConfigManager().colorize("&6&l====== &e&l" + targetPlayerName + " &6&l======"));
        viewer.sendMessage(plugin.getConfigManager().getMessage("xp-stats-personal",
                "player", targetPlayerName,
                "xp", String.valueOf(xp),
                "position", String.valueOf(position)));
    }
}