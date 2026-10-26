package bnthedev.rajce.pro.ketchupBuildXp;

import bnthedev.rajce.pro.ketchupBuildXp.commands.*;
import bnthedev.rajce.pro.ketchupBuildXp.listeners.*;
import bnthedev.rajce.pro.ketchupBuildXp.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class KetchupBuildXp extends JavaPlugin {

    private static KetchupBuildXp instance;
    private XpManager xpManager;
    private ConfigManager configManager;
    private AfkManager afkManager;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        xpManager = new XpManager(this);
        afkManager = new AfkManager(this);

        registerCommands();

        registerListeners();

        getLogger().info("KetchupBuildXp plugin byl úspěšně načten!");
    }

    @Override
    public void onDisable() {
        if (xpManager != null) {
            xpManager.saveData();
        }
        getLogger().info("KetchupBuildXp plugin byl vypnut!");
    }

    private void registerCommands() {
        getCommand("xpstats").setExecutor(new XpStatsCommand(this));
        getCommand("xpmenu").setExecutor(new XpMenuCommand(this));
        getCommand("xpremove").setExecutor(new XpRemoveCommand(this));
        getCommand("xpreset").setExecutor(new XpResetCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerCommandListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerActivityListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new AxiomActivityListener(this), this);

    }

    public static KetchupBuildXp getInstance() {
        return instance;
    }

    public XpManager getXpManager() {
        return xpManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public AfkManager getAfkManager() {
        return afkManager;
    }
}