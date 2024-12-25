package hu.kxtsoo.axBlockLimiter.database.impl;

import hu.kxtsoo.axBlockLimiter.AxBlockLimiter;
import hu.kxtsoo.axBlockLimiter.database.DatabaseInterface;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite implements DatabaseInterface {
    private final JavaPlugin plugin;
    private Connection connection;

    public SQLite(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() throws SQLException {
        File dataFolder = AxBlockLimiter.getInstance().getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        String url = "jdbc:sqlite:" + new File(dataFolder, "data.db").getAbsolutePath();
        connection = DriverManager.getConnection(url);

        createTables();
    }

    @Override
    public void createTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS axblocklimiter_players (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "ip_address TEXT)";
            statement.execute(sql);
        }
    }

    @Override
    public void incrementChunkBlockCount(int chunkX, int chunkZ, Material blockType) throws SQLException {

    }

    @Override
    public void decrementChunkBlockCount(int chunkX, int chunkZ, Material blockType) throws SQLException {

    }

    @Override
    public int getChunkBlockCount(int chunkX, int chunkZ, Material blockType) throws SQLException {
        return 0;
    }

    @Override
    public void setChunkBlockCount(int chunkX, int chunkZ, Material blockType, int count) throws SQLException {

    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
