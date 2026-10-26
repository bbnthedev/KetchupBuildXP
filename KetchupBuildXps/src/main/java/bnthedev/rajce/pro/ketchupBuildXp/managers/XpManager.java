package bnthedev.rajce.pro.ketchupBuildXp.managers;

import bnthedev.rajce.pro.ketchupBuildXp.KetchupBuildXp;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class XpManager {

    private final KetchupBuildXp plugin;
    private File dataFile;
    private FileConfiguration dataConfig;

    private final Map<UUID, Integer> commandCounts = new HashMap<>();
    private final Map<UUID, Integer> axiomEditCounts = new HashMap<>();
    private final Map<UUID, Long> lastActivity = new HashMap<>();
    private final Map<UUID, Long> sessionStart = new HashMap<>();
    private final Map<UUID, Long> accumulatedTime = new HashMap<>();

    public XpManager(KetchupBuildXp plugin) {
        this.plugin = plugin;
        setupDataFile();
        loadData();
        startHourlyRewardTask();
    }

    private void setupDataFile() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        dataFile = new File(plugin.getDataFolder(), "xpdata.yml");

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Nepodařilo se vytvořit xpdata.yml: " + e.getMessage());
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void loadData() {
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        if (dataConfig.getConfigurationSection("players") != null) {
            for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    long accumulated = dataConfig.getLong("players." + uuidString + ".accumulatedTime", 0L);
                    if (accumulated > 0) {
                        accumulatedTime.put(uuid, accumulated);
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    public void saveData() {
        for (Map.Entry<UUID, Long> entry : accumulatedTime.entrySet()) {
            String uuidString = entry.getKey().toString();
            dataConfig.set("players." + uuidString + ".accumulatedTime", entry.getValue());
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Nepodařilo se uložit XP data: " + e.getMessage());
        }
    }

    public void addCommandCount(Player player) {
        UUID uuid = player.getUniqueId();

        updateActivity(player);

        int currentCount = commandCounts.getOrDefault(uuid, 0) + 1;
        commandCounts.put(uuid, currentCount);

        int commandsPerReward = plugin.getConfigManager().getInt("xp.commands-per-reward");
        if (currentCount >= commandsPerReward) {
            int reward = plugin.getConfigManager().getInt("xp.command-reward");
            addXp(player, reward);
            commandCounts.put(uuid, 0);
        }
    }

    public void addAxiomEditCount(Player player) {
        UUID uuid = player.getUniqueId();

        updateActivity(player);

        int currentCount = axiomEditCounts.getOrDefault(uuid, 0) + 1;
        axiomEditCounts.put(uuid, currentCount);

        int editsPerReward = plugin.getConfigManager().getInt("xp.axiom-edits-per-reward");
        if (currentCount >= editsPerReward) {
            int reward = plugin.getConfigManager().getInt("xp.axiom-edit-reward");
            addXp(player, reward);
            axiomEditCounts.put(uuid, 0);
        }
    }

    public void addXp(Player player, int amount) {
        String path = "players." + player.getUniqueId().toString();
        int currentXp = dataConfig.getInt(path + ".xp", 0);
        dataConfig.set(path + ".xp", currentXp + amount);
        dataConfig.set(path + ".name", player.getName());
        dataConfig.set(path + ".lastUpdate", System.currentTimeMillis());

        saveData();
    }

    public int getXp(Player player) {
        return getXp(player.getUniqueId());
    }

    public int getXp(UUID uuid) {
        return dataConfig.getInt("players." + uuid.toString() + ".xp", 0);
    }

    public int getXp(String playerName) {
        if (dataConfig.getConfigurationSection("players") == null) {
            return 0;
        }

        for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
            String name = dataConfig.getString("players." + uuidString + ".name");
            if (name != null && name.equalsIgnoreCase(playerName)) {
                return dataConfig.getInt("players." + uuidString + ".xp", 0);
            }
        }
        return 0;
    }

    public List<Map.Entry<String, Integer>> getTopPlayers() {
        Map<String, Integer> playerXp = new HashMap<>();

        if (dataConfig.getConfigurationSection("players") == null) {
            return new ArrayList<>();
        }

        for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
            String name = dataConfig.getString("players." + uuidString + ".name");
            int xp = dataConfig.getInt("players." + uuidString + ".xp");
            if (name != null) {
                playerXp.put(name, xp);
            }
        }

        return playerXp.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());
    }

    public int getPlayerPosition(String playerName) {
        List<Map.Entry<String, Integer>> topPlayers = getTopPlayers();
        for (int i = 0; i < topPlayers.size(); i++) {
            if (topPlayers.get(i).getKey().equalsIgnoreCase(playerName)) {
                return i + 1;
            }
        }
        return -1;
    }

    public boolean removePlayer(String playerName) {
        if (dataConfig.getConfigurationSection("players") == null) {
            return false;
        }

        for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
            String name = dataConfig.getString("players." + uuidString + ".name");
            if (name != null && name.equalsIgnoreCase(playerName)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    commandCounts.remove(uuid);
                    axiomEditCounts.remove(uuid);
                    lastActivity.remove(uuid);
                    sessionStart.remove(uuid);
                    accumulatedTime.remove(uuid);
                } catch (IllegalArgumentException ignored) {
                }

                dataConfig.set("players." + uuidString, null);
                saveData();
                return true;
            }
        }
        return false;
    }

    public void resetAllData() {
        dataConfig.set("players", null);
        commandCounts.clear();
        axiomEditCounts.clear();
        lastActivity.clear();
        sessionStart.clear();
        accumulatedTime.clear();
        saveData();
    }

    public void updateActivity(Player player) {
        UUID uuid = player.getUniqueId();
        lastActivity.put(uuid, System.currentTimeMillis());

        if (!sessionStart.containsKey(uuid)) {
            sessionStart.put(uuid, System.currentTimeMillis());
        }
    }

    public void startSession(Player player) {
        UUID uuid = player.getUniqueId();

        if (!accumulatedTime.containsKey(uuid)) {
            accumulatedTime.put(uuid, 0L);
        }

        sessionStart.put(uuid, System.currentTimeMillis());
        lastActivity.put(uuid, System.currentTimeMillis());
    }

    public void endSession(Player player) {
        UUID uuid = player.getUniqueId();

        if (sessionStart.containsKey(uuid)) {
            long sessionStartTime = sessionStart.get(uuid);
            long currentTime = System.currentTimeMillis();
            long sessionDuration = currentTime - sessionStartTime;

            long currentAccumulated = accumulatedTime.getOrDefault(uuid, 0L);
            long newAccumulated = currentAccumulated + sessionDuration;
            accumulatedTime.put(uuid, newAccumulated);

            String path = "players." + uuid.toString();
            dataConfig.set(path + ".accumulatedTime", newAccumulated);
        }

        sessionStart.remove(uuid);
        lastActivity.remove(uuid);
        commandCounts.remove(uuid);
        axiomEditCounts.remove(uuid);

        saveData();
    }

    public void pauseSession(Player player) {
        UUID uuid = player.getUniqueId();

        if (sessionStart.containsKey(uuid)) {
            long sessionStartTime = sessionStart.get(uuid);
            long currentTime = System.currentTimeMillis();
            long sessionDuration = currentTime - sessionStartTime;

            long currentAccumulated = accumulatedTime.getOrDefault(uuid, 0L);
            long newAccumulated = currentAccumulated + sessionDuration;
            accumulatedTime.put(uuid, newAccumulated);

            String path = "players." + uuid.toString();
            dataConfig.set(path + ".accumulatedTime", newAccumulated);
            saveData();

            sessionStart.remove(uuid);
        }
    }

    public void resumeSession(Player player) {
        UUID uuid = player.getUniqueId();

        if (!sessionStart.containsKey(uuid)) {
            sessionStart.put(uuid, System.currentTimeMillis());
        }
    }

    public long getLastActivity(Player player) {
        return lastActivity.getOrDefault(player.getUniqueId(), 0L);
    }

    private void startHourlyRewardTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            int hourlyReward = plugin.getConfigManager().getInt("xp.hourly-reward");

            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();

                if (plugin.getAfkManager().isAfk(player)) {
                    continue;
                }

                long accumulated = accumulatedTime.getOrDefault(uuid, 0L);

                if (sessionStart.containsKey(uuid)) {
                    long sessionStartTime = sessionStart.get(uuid);
                    long currentSessionTime = currentTime - sessionStartTime;
                    accumulated += currentSessionTime;
                }

                if (accumulated >= 3600000) {
                    addXp(player, hourlyReward);

                    accumulatedTime.put(uuid, 0L);
                    sessionStart.put(uuid, currentTime);

                    String path = "players." + uuid.toString();
                    dataConfig.set(path + ".accumulatedTime", 0L);
                    try {
                        dataConfig.save(dataFile);
                    } catch (IOException e) {
                        plugin.getLogger().severe("Chyba při ukládání accumulatedTime: " + e.getMessage());
                    }

                    String message = plugin.getConfigManager().getMessage("xp-gained", "amount", String.valueOf(hourlyReward));
                    player.sendMessage(message);
                }
            }
        }, 20L * 60L, 20L * 60L);
    }

    public long getAccumulatedMinutes(Player player) {
        UUID uuid = player.getUniqueId();
        long accumulated = accumulatedTime.getOrDefault(uuid, 0L);

        if (sessionStart.containsKey(uuid) && !plugin.getAfkManager().isAfk(player)) {
            long currentSessionTime = System.currentTimeMillis() - sessionStart.get(uuid);
            accumulated += currentSessionTime;
        }

        return accumulated / 60000;
    }
}