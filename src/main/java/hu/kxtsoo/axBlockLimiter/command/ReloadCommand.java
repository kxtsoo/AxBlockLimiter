package hu.kxtsoo.axBlockLimiter.command;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import hu.kxtsoo.axBlockLimiter.AxBlockLimiter;
import hu.kxtsoo.axBlockLimiter.database.DatabaseManager;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

@Command("axblocklimiter")
@Permission("axblocklimiter.admin")
public class ReloadCommand extends BaseCommand {

    @SubCommand("reload")
    @Permission("axblocklimiter.admin.reload")
    public void reload(CommandSender sender) throws SQLException {
        AxBlockLimiter.getInstance().getConfigUtil().reloadConfig();
        DatabaseManager.initialize(AxBlockLimiter.getInstance().getConfigUtil(), AxBlockLimiter.getInstance());

        sender.sendMessage(AxBlockLimiter.getInstance().getConfigUtil().getMessage("messages.reload-command-success"));
    }
}