package bnthedev.rajce.pro.ketchupBuildXp.managers;

import bnthedev.rajce.pro.ketchupBuildXp.KetchupBuildXp;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigManager {

    private final KetchupBuildXp plugin;
    private File configFile;
    private FileConfiguration config;

    private final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public ConfigManager(KetchupBuildXp plugin) {
        this.plugin = plugin;
        setupConfig();
    }

    private void setupConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            createDefaultConfig();
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void createDefaultConfig() {
        try {
            configFile.createNewFile();
            config = YamlConfiguration.loadConfiguration(configFile);

            config.set("xp.command-reward", 5);
            config.set("xp.hourly-reward", 100);
            config.set("xp.commands-per-reward", 10);

            config.set("afk.timeout-minutes", 5);

            config.set("messages.xp-gained", "&a&lXP+ &7Získal jsi &e%amount%xp &7za aktivitu!");
            config.set("messages.afk-warning", "&c&lAFK &7Jsi nyní AFK a nepočítá se ti aktivita!");
            config.set("messages.afk-return", "&a&lZPĚT &7Vítej zpět! Aktivita se ti opět počítá.");
            config.set("messages.xp-stats-header", "&6&l====== &e&lXP STATISTIKY &6&l======");
            config.set("messages.xp-stats-player", "&7%position%. &e%player% &7- &a%xp%xp");
            config.set("messages.xp-stats-personal", "&e%player% &7má &a%xp%xp &7(Pozice: &e#%position%&7)");
            config.set("messages.xp-stats-not-found", "&cHráč nebyl nalezen v databázi!");
            config.set("messages.xp-menu-title", "&6&lXP Menu - Buildři");
            config.set("messages.xp-menu-lore", Arrays.asList(
                    "&7XP: &a%xp%",
                    "&7Pozice: &e#%position%",
                    "&7Klikni pro více info!"
            ));
            config.set("messages.xp-removed", "&aHráč &e%player% &abyl odebrán ze seznamu!");
            config.set("messages.xp-reset", "&aVšechna XP data byla vyresetována!");
            config.set("messages.no-permission", "&cNemáš oprávnění k použití tohoto příkazu!");
            config.set("messages.player-not-found", "&cHráč nebyl nalezen!");
            config.set("messages.invalid-usage", "&cNesprávné použití! Použij: %usage%");

            config.set("gui.menu-size", 54);
            config.set("gui.menu-title", "&6&lXP Menu - Buildři");

            config.save(configFile);
            plugin.getLogger().info("Výchozí konfigurace byla vytvořena!");

            config.set("xp.axiom-edit-reward", 5);
            config.set("xp.axiom-edits-per-reward", 50);

        } catch (IOException e) {
            plugin.getLogger().severe("Chyba při vytváření konfigurace: " + e.getMessage());
        }
    }

    public String colorize(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        while (matcher.find()) {
            String color = matcher.group(1);
            message = message.replace("&#" + color, ChatColor.of("#" + color).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessage(String path) {
        return colorize(config.getString("messages." + path, "&cChybí zpráva: " + path));
    }

    public String getMessage(String path, String... placeholders) {
        String message = getMessage(path);
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("%" + placeholders[i] + "%", placeholders[i + 1]);
            }
        }
        return message;
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }
}