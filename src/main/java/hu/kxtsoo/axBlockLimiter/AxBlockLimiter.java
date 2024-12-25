package hu.kxtsoo.axBlockLimiter;

import hu.kxtsoo.axBlockLimiter.database.DatabaseManager;
import hu.kxtsoo.axBlockLimiter.hook.HookManager;
import hu.kxtsoo.axBlockLimiter.listener.BlockBreakListener;
import hu.kxtsoo.axBlockLimiter.listener.BlockPlaceListener;
import hu.kxtsoo.axBlockLimiter.manager.CommandManager;
import hu.kxtsoo.axBlockLimiter.util.ConfigUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

public final class AxBlockLimiter extends JavaPlugin {

    private static AxBlockLimiter instance;
    private ConfigUtil configUtil;
    private HookManager hookManager;

    @Override
    public void onEnable() {
        instance = this;

        configUtil = new ConfigUtil(this);
        configUtil.setupConfig();

        try {
            DatabaseManager.initialize(configUtil, this);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to initialize database", e);
        }

        hookManager = new HookManager(this, configUtil);
        hookManager.registerHooks();

        getServer().getPluginManager().registerEvents(new BlockPlaceListener(configUtil), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(configUtil), this);

        CommandManager commandManager = new CommandManager(this, configUtil);
        commandManager.registerSuggestions();
        commandManager.registerCommands();

    }

    @Override
    public void onDisable() {
        try {
            DatabaseManager.close();
        } catch (SQLException e) {
            getLogger().severe("Failed to close the database: " + e.getMessage());
        }
    }

    public static AxBlockLimiter getInstance() {
        return instance;
    }

    public ConfigUtil getConfigUtil() {
        return configUtil;
    }

    public HookManager getHookManager() {
        return hookManager;
    }
}