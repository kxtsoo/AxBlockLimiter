package hu.kxtsoo.axBlockLimiter.manager;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import hu.kxtsoo.axBlockLimiter.AxBlockLimiter;
import hu.kxtsoo.axBlockLimiter.command.ReloadCommand;
import hu.kxtsoo.axBlockLimiter.util.ConfigUtil;
import org.bukkit.command.CommandSender;

public class CommandManager {
    private final BukkitCommandManager<CommandSender> commandManager;
    private ConfigUtil configUtil;
    private AxBlockLimiter plugin;

    public CommandManager(AxBlockLimiter plugin, ConfigUtil configUtil) {
        this.commandManager = BukkitCommandManager.create(plugin);
        this.configUtil = configUtil;
        this.plugin = plugin;
    }

    public void registerSuggestions() {}

    public void registerCommands() {
        commandManager.registerCommand(new ReloadCommand());
    }
}
