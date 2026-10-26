package bnthedev.rajce.pro.ketchupBuildXp.listeners;

import bnthedev.rajce.pro.ketchupBuildXp.KetchupBuildXp;
import com.moulberry.axiom.event.AxiomModifyWorldEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AxiomActivityListener implements Listener {

    private final KetchupBuildXp plugin;

    public AxiomActivityListener(KetchupBuildXp plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAxiomEdit(AxiomModifyWorldEvent event) {
        Player player = event.getPlayer();

        plugin.getAfkManager().updateActivity(player);
        plugin.getXpManager().addAxiomEditCount(player);
    }
}