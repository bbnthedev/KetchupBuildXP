package bnthedev.rajce.pro.ketchupBuildXp.commands;

import bnthedev.rajce.pro.ketchupBuildXp.KetchupBuildXp;
import bnthedev.rajce.pro.ketchupBuildXp.gui.XpMenuGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class XpMenuCommand implements CommandExecutor {

    private final KetchupBuildXp plugin;

    public XpMenuCommand(KetchupBuildXp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().colorize("&cTento příkaz může použít pouze hráč!"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("ketchupbuildxp.xpmenu")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        XpMenuGUI gui = new XpMenuGUI(plugin);
        gui.openMenu(player);

        return true;
    }
}