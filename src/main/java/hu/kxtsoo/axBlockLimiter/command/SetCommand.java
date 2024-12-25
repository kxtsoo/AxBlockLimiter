package hu.kxtsoo.axBlockLimiter.command;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import hu.kxtsoo.axBlockLimiter.database.DatabaseManager;
import hu.kxtsoo.axBlockLimiter.util.ConfigUtil;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

@Command("axblocklimiter")
@Permission("axblocklimiter.admin")
public class SetCommand extends BaseCommand {

    private final ConfigUtil configUtil;

    public SetCommand(ConfigUtil configUtil) {
        this.configUtil = configUtil;
    }

    @SubCommand("setcount")
    @Permission("axblocklimiter.admin.setcount")
    public void setBlockCount(CommandSender sender, Player target, Material blockType, int count) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(configUtil.getMessage("messages.command-only-for-players"));
            return;
        }

        Player player = (Player) sender;
        Chunk chunk = player.getLocation().getChunk();

        try {
            DatabaseManager.setChunkBlockCount(chunk.getX(), chunk.getZ(), blockType, count);
            player.sendMessage(configUtil.getMessage("messages.set-command.block-count-set-success")
                    .replace("%chunk%", chunk.getX() + ", " + chunk.getZ())
                    .replace("%block%", blockType.name())
                    .replace("%count%", String.valueOf(count)));
        } catch (SQLException e) {
            player.sendMessage(configUtil.getMessage("messages.set-command.block-count-set-failure"));
            e.printStackTrace();
        }
    }
}