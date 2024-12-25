package hu.kxtsoo.axBlockLimiter.listener;

import hu.kxtsoo.axBlockLimiter.AxBlockLimiter;
import hu.kxtsoo.axBlockLimiter.database.DatabaseManager;
import hu.kxtsoo.axBlockLimiter.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.sql.SQLException;
import java.util.List;

public class BlockPlaceListener implements Listener {

    private final ConfigUtil configUtil;

    public BlockPlaceListener(ConfigUtil configUtil) {
        this.configUtil = configUtil;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Material blockType = event.getBlockPlaced().getType();
        Chunk chunk = event.getBlock().getChunk();

        if (AxBlockLimiter.getInstance().getHookManager().getRegionHook() != null) {
            if (AxBlockLimiter.getInstance().getHookManager().getRegionHook().isInProtectedRegion(event.getBlock().getLocation())) {
                if (!AxBlockLimiter.getInstance().getHookManager().getRegionHook().canBuild(player, event.getBlock().getLocation())) {
                    if(!configUtil.getMessage("messages.no-build-permission").isEmpty()) {
                        player.sendActionBar(configUtil.getMessage("messages.no-build-permission"));
                    }
                    event.setCancelled(true);
                    return;
                }
            }
        }

        String groupKey = configUtil.getGroupKey(blockType);

        if (!configUtil.getConfig().contains("limits." + groupKey)) {
            return;
        }

        int limit = configUtil.getConfig().getInt("limits." + groupKey + ".default", -1);

        try {
            int currentCount = getCurrentChunkCount(chunk, groupKey);

            if (currentCount >= limit) {
                event.setCancelled(true);
                player.sendActionBar(configUtil.getMessage("messages.block-limit-reached")
                        .replace("%current%", String.valueOf(currentCount))
                        .replace("%limit%", String.valueOf(limit))
                        .replace("%block%", blockType.name()));
                return;
            }

            Bukkit.getScheduler().runTaskAsynchronously(AxBlockLimiter.getInstance(), () -> {
                try {
                    DatabaseManager.incrementChunkBlockCount(chunk.getX(), chunk.getZ(), blockType);

                    Bukkit.getScheduler().runTask(AxBlockLimiter.getInstance(), () -> player.sendActionBar(
                            configUtil.getMessage("messages.block-status")
                                    .replace("%current%", String.valueOf(currentCount + 1))
                                    .replace("%limit%", String.valueOf(limit))
                                    .replace("%chunk%", chunk.getX() + ", " + chunk.getZ())
                                    .replace("%block%", blockType.name())
                    ));
                } catch (SQLException e) {
                    Bukkit.getScheduler().runTask(AxBlockLimiter.getInstance(), () -> player.sendMessage("An error occurred while updating the block count."));
                    e.printStackTrace();
                }
            });

        } catch (SQLException e) {
            player.sendMessage("An error occurred while checking the block count.");
            e.printStackTrace();
        }
    }

    private int getCurrentChunkCount(Chunk chunk, String groupKey) throws SQLException {
        int currentCount = 0;
        List<Material> groupMaterials = configUtil.getGroupMaterials(groupKey);
        for (Material material : groupMaterials) {
            currentCount += DatabaseManager.getChunkBlockCount(chunk.getX(), chunk.getZ(), material);
        }
        return currentCount;
    }
}
