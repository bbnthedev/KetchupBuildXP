package bnthedev.rajce.pro.ketchupBuildXp.managers;

import bnthedev.rajce.pro.ketchupBuildXp.KetchupBuildXp;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AfkManager {

    private final KetchupBuildXp plugin;
    private final Map<UUID, Boolean> afkPlayers = new HashMap<>();
    private final Map<UUID, Long> afkWarningsSent = new HashMap<>();

    public AfkManager(KetchupBuildXp plugin) {
        this.plugin = plugin;
        startAfkCheckTask();
    }

    public boolean isAfk(Player player) {
        return afkPlayers.getOrDefault(player.getUniqueId(), false);
    }

    public void setAfk(Player player, boolean afk) {
        UUID uuid = player.getUniqueId();
        boolean wasAfk = afkPlayers.getOrDefault(uuid, false);

        afkPlayers.put(uuid, afk);

        if (afk && !wasAfk) {
            plugin.getXpManager().pauseSession(player);

            String message = plugin.getConfigManager().getMessage("afk-warning");
            player.sendMessage(message);
            afkWarningsSent.put(uuid, System.currentTimeMillis());
        } else if (!afk && wasAfk) {
            plugin.getXpManager().resumeSession(player);

            String message = plugin.getConfigManager().getMessage("afk-return");
            player.sendMessage(message);
            afkWarningsSent.remove(uuid);
        }
    }

    public void updateActivity(Player player) {
        if (isAfk(player)) {
            setAfk(player, false);
        }
        plugin.getXpManager().updateActivity(player);
    }

    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        afkPlayers.remove(uuid);
        afkWarningsSent.remove(uuid);
    }

    private void startAfkCheckTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            int afkTimeoutMinutes = plugin.getConfigManager().getInt("afk.timeout-minutes");
            long afkTimeout = afkTimeoutMinutes * 60 * 1000L;

            for (Player player : plugin.getServer().getOnlinePlayers()) {
                long lastActivity = plugin.getXpManager().getLastActivity(player);

                if (lastActivity > 0 && (currentTime - lastActivity) >= afkTimeout) {
                    if (!isAfk(player)) {
                        setAfk(player, true);
                    }
                }
            }
        }, 20L * 30L, 20L * 30L);
    }
}