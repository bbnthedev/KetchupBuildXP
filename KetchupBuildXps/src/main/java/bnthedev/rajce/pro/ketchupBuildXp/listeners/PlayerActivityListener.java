package bnthedev.rajce.pro.ketchupBuildXp.listeners;

import bnthedev.rajce.pro.ketchupBuildXp.KetchupBuildXp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

public class PlayerActivityListener implements Listener {

    private final KetchupBuildXp plugin;

    public PlayerActivityListener(KetchupBuildXp plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getXpManager().startSession(player);
        plugin.getAfkManager().updateActivity(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getXpManager().endSession(player);
        plugin.getAfkManager().removePlayer(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {

            plugin.getAfkManager().updateActivity(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getAfkManager().updateActivity(player);
        });
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        plugin.getAfkManager().updateActivity(event.getPlayer());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        plugin.getAfkManager().updateActivity(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer() != null) {
            plugin.getAfkManager().updateActivity(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        plugin.getAfkManager().updateActivity(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        plugin.getAfkManager().updateActivity(event.getPlayer());
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        plugin.getAfkManager().updateActivity(event.getPlayer());
    }
}