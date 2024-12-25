package hu.kxtsoo.axBlockLimiter.command;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import hu.kxtsoo.axBlockLimiter.AxBlockLimiter;
import hu.kxtsoo.axBlockLimiter.database.DatabaseManager;
import hu.kxtsoo.axBlockLimiter.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Command("axblocklimiter")
@Permission("axblocklimiter.admin")
public class RecalculateCommand extends BaseCommand {

    private final ConfigUtil configUtil;

    public RecalculateCommand(ConfigUtil configUtil) {
        this.configUtil = configUtil;
    }

    @SubCommand("recalculate")
    @Permission("axblocklimiter.admin.recalculate")
    public void recalculate(CommandSender sender, String worldName, int chunkX, int chunkZ) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            sender.sendMessage(configUtil.getMessage("messages.recalculate-command.invalid-world"));
            return;
        }

        Chunk chunk = world.getChunkAt(chunkX, chunkZ);
        if (!chunk.isLoaded()) {
            chunk.load();
        }

        Bukkit.getScheduler().runTaskAsynchronously(AxBlockLimiter.getInstance(), () -> {
            try {
                Map<String, List<Material>> limits = configUtil.getConfiguredGroups();
                for (Map.Entry<String, List<Material>> entry : limits.entrySet()) {
                    String groupKey = entry.getKey();
                    List<Material> groupMaterials = entry.getValue();

                    int actualCount = countBlocksInChunk(chunk, groupMaterials);
                    int databaseCount = getCurrentChunkCount(chunk, groupMaterials);

                    if (actualCount != databaseCount) {
                        for (Material material : groupMaterials) {
                            DatabaseManager.setChunkBlockCount(chunk.getX(), chunk.getZ(), material, actualCount);
                        }
                        sender.sendMessage(configUtil.getMessage("messages.recalculate-command.recalculation-update")
                                .replace("%chunk%", chunkX + ", " + chunkZ)
                                .replace("%block%", groupKey)
                                .replace("%old%", String.valueOf(databaseCount))
                                .replace("%new%", String.valueOf(actualCount)));
                    }
                }

                sender.sendMessage(configUtil.getMessage("messages.recalculate-command.recalculation-success")
                        .replace("%chunk%", chunkX + ", " + chunkZ));
            } catch (SQLException e) {
                sender.sendMessage(configUtil.getMessage("messages.recalculate-command.recalculation-failure"));
                e.printStackTrace();
            }
        });
    }

    private int countBlocksInChunk(Chunk chunk, List<Material> groupMaterials) {
        int count = 0;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = chunk.getWorld().getMinHeight(); y < chunk.getWorld().getMaxHeight(); y++) {
                    Material blockType = chunk.getBlock(x, y, z).getType();
                    if (groupMaterials.contains(blockType)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private int getCurrentChunkCount(Chunk chunk, List<Material> groupMaterials) throws SQLException {
        int currentCount = 0;
        for (Material material : groupMaterials) {
            currentCount += DatabaseManager.getChunkBlockCount(chunk.getX(), chunk.getZ(), material);
        }
        return currentCount;
    }
}
