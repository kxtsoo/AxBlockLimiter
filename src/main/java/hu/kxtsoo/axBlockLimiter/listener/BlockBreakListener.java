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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.List;

public class BlockBreakListener implements Listener {

    private final ConfigUtil configUtil;

    public BlockBreakListener(ConfigUtil configUtil) {
        this.configUtil = configUtil;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material blockType = event.getBlock().getType();
        Chunk chunk = event.getBlock().getChunk();

        String groupKey = configUtil.getGroupKey(blockType);

        if (!configUtil.getConfig().contains("limits." + groupKey)) {
            return;
        }

        int limit = configUtil.getConfig().getInt("limits." + groupKey + ".default", -1);

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    DatabaseManager.decrementChunkBlockCount(chunk.getX(), chunk.getZ(), blockType);

                    List<Material> groupMaterials = configUtil.getGroupMaterials(groupKey);
                    int currentCount = 0;
                    for (Material material : groupMaterials) {
                        currentCount += DatabaseManager.getChunkBlockCount(chunk.getX(), chunk.getZ(), material);
                    }

                    int finalCurrentCount = currentCount;
                    Bukkit.getScheduler().runTask(AxBlockLimiter.getInstance(), () -> player.sendActionBar(
                            configUtil.getMessage("messages.block-status")
                                    .replace("%current%", String.valueOf(finalCurrentCount))
                                    .replace("%limit%", String.valueOf(limit))
                                    .replace("%chunk%", chunk.getX() + ", " + chunk.getZ())
                                    .replace("%block%", blockType.name())
                    ));
                } catch (SQLException e) {
                    Bukkit.getScheduler().runTask(AxBlockLimiter.getInstance(), () -> player.sendMessage("An error occurred while updating the block count."));
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(AxBlockLimiter.getInstance());
    }
}