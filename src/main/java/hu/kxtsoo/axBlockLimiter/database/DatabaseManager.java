package hu.kxtsoo.axBlockLimiter.database;

import hu.kxtsoo.axBlockLimiter.database.impl.H2;
import hu.kxtsoo.axBlockLimiter.database.impl.MySQL;
import hu.kxtsoo.axBlockLimiter.database.impl.SQLite;
import hu.kxtsoo.axBlockLimiter.util.ConfigUtil;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.UUID;

public class DatabaseManager {
    private static DatabaseInterface database;

    public static void initialize(ConfigUtil configUtil, JavaPlugin plugin) throws SQLException {
        String driver = configUtil.getConfig().getString("storage.driver", "h2");
        switch (driver.toLowerCase()) {
            case "sqlite":
                database = new SQLite(plugin);
                database.initialize();
                break;
            case "mysql":
                database = new MySQL(configUtil, plugin);
                database.initialize();
                break;
            case "h2":
                database = new H2(plugin, configUtil);
                database.initialize();
                break;
            default:
                throw new IllegalArgumentException("Unsupported database driver: " + driver);
        }
        database.createTables();
    }

    public static void incrementChunkBlockCount(int chunkX, int chunkZ, Material blockType) throws SQLException {
        database.incrementChunkBlockCount(chunkX, chunkZ, blockType);
    }

    public static void decrementChunkBlockCount(int chunkX, int chunkZ, Material blockType) throws SQLException {
        database.decrementChunkBlockCount(chunkX, chunkZ, blockType);
    }

    public static int getChunkBlockCount(int chunkX, int chunkZ, Material blockType) throws SQLException {
        return database.getChunkBlockCount(chunkX, chunkZ, blockType);
    }

    public static void setChunkBlockCount(int chunkX, int chunkZ, Material blockType, int count) throws SQLException {
        database.setChunkBlockCount(chunkX, chunkZ, blockType, count);
    }

    public static void close() throws SQLException {
        if (database != null) {
            database.close();
        }
    }
}