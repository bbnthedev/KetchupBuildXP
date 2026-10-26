package bnthedev.rajce.pro.ketchupBuildXp.commands;

import bnthedev.rajce.pro.ketchupBuildXp.KetchupBuildXp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class XpStatsCommand implements CommandExecutor {

    private final KetchupBuildXp plugin;

    public XpStatsCommand(KetchupBuildXp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("ketchupbuildxp.xpstats")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            showTopPlayers(sender);
        } else if (args.length == 1) {
            showPlayerStats(sender, args[0]);
        } else {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage", "usage", "/xpstats [nick]"));
        }

        return true;
    }

    private void showTopPlayers(CommandSender sender) {
        List<Map.Entry<String, Integer>> topPlayers = plugin.getXpManager().getTopPlayers();

        if (topPlayers.isEmpty()) {
            sender.sendMessage(plugin.getConfigManager().getMessage("xp-stats-not-found"));
            return;
        }

        sender.sendMessage(plugin.getConfigManager().getMessage("xp-stats-header"));

        for (int i = 0; i < topPlayers.size(); i++) {
            Map.Entry<String, Integer> entry = topPlayers.get(i);
            String message = plugin.getConfigManager().getMessage("xp-stats-player",
                    "position", String.valueOf(i + 1),
                    "player", entry.getKey(),
                    "xp", String.valueOf(entry.getValue()));
            sender.sendMessage(message);
        }
    }

    private void showPlayerStats(CommandSender sender, String playerName) {
        int xp = plugin.getXpManager().getXp(playerName);
        int position = plugin.getXpManager().getPlayerPosition(playerName);

        if (position == -1) {
            sender.sendMessage(plugin.getConfigManager().getMessage("xp-stats-not-found"));
            return;
        }

        String message = plugin.getConfigManager().getMessage("xp-stats-personal",
                "player", playerName,
                "xp", String.valueOf(xp),
                "position", String.valueOf(position));
        sender.sendMessage(message);
    }
}