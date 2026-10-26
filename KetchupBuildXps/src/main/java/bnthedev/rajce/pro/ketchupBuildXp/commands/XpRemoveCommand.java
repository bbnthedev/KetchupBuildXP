package bnthedev.rajce.pro.ketchupBuildXp.commands;

import bnthedev.rajce.pro.ketchupBuildXp.KetchupBuildXp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class XpRemoveCommand implements CommandExecutor {

    private final KetchupBuildXp plugin;

    public XpRemoveCommand(KetchupBuildXp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("ketchupbuildxp.xpremove")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage", "usage", "/xpremove <nick>"));
            return true;
        }

        String playerName = args[0];

        if (plugin.getXpManager().removePlayer(playerName)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("xp-removed", "player", playerName));
        } else {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
        }

        return true;
    }
}