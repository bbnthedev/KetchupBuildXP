package bnthedev.rajce.pro.ketchupBuildXp.listeners;

import bnthedev.rajce.pro.ketchupBuildXp.KetchupBuildXp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandListener implements Listener {

    private final KetchupBuildXp plugin;

    public PlayerCommandListener(KetchupBuildXp plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        plugin.getAfkManager().updateActivity(player);

        plugin.getXpManager().addCommandCount(player);
    }
}