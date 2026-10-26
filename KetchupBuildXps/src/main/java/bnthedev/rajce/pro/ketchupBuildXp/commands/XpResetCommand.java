package bnthedev.rajce.pro.ketchupBuildXp.commands;

import bnthedev.rajce.pro.ketchupBuildXp.KetchupBuildXp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class XpResetCommand implements CommandExecutor {

    private final KetchupBuildXp plugin;

    public XpResetCommand(KetchupBuildXp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("ketchupbuildxp.xpreset")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(plugin.getConfigManager().colorize("&cOpravdu chceš vyresetovat všechna XP data? Použij &e/xpreset confirm"));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
            plugin.getXpManager().resetAllData();
            sender.sendMessage(plugin.getConfigManager().getMessage("xp-reset"));
        } else {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage", "usage", "/xpreset confirm"));
        }

        return true;
    }
}