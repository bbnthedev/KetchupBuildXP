package bnthedev.rajce.pro.ketchupBuildXp.gui;

import bnthedev.rajce.pro.ketchupBuildXp.KetchupBuildXp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XpMenuGUI {

    private final KetchupBuildXp plugin;

    public XpMenuGUI(KetchupBuildXp plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player) {
        int menuSize = plugin.getConfigManager().getInt("gui.menu-size");
        String title = plugin.getConfigManager().getMessage("gui.menu-title");

        Inventory gui = Bukkit.createInventory(null, menuSize, ChatColor.YELLOW + "XP Statistiky");

        List<Map.Entry<String, Integer>> topPlayers = plugin.getXpManager().getTopPlayers();

        for (int i = 0; i < topPlayers.size() && i < menuSize; i++) {
            Map.Entry<String, Integer> entry = topPlayers.get(i);
            String playerName = entry.getKey();
            int xp = entry.getValue();
            int position = i + 1;

            ItemStack skull = createPlayerSkull(playerName, xp, position);
            gui.setItem(i, skull);
        }

        fillEmptySlots(gui);

        player.openInventory(gui);
    }

    private ItemStack createPlayerSkull(String playerName, int xp, int position) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(plugin.getConfigManager().colorize("&e&l" + playerName));

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            meta.setOwningPlayer(offlinePlayer);

            List<String> loreTemplate = plugin.getConfigManager().getConfig().getStringList("messages.xp-menu-lore");
            List<String> lore = loreTemplate.stream()
                    .map(line -> plugin.getConfigManager().colorize(line
                            .replace("%xp%", String.valueOf(xp))
                            .replace("%position%", String.valueOf(position))))
                    .collect(Collectors.toList());

            meta.setLore(lore);
            skull.setItemMeta(meta);
        }

        return skull;
    }

    private void fillEmptySlots(Inventory gui) {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            filler.setItemMeta(meta);
        }

        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }
    }
}