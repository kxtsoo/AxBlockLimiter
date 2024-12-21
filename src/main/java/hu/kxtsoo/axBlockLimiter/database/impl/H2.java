package hu.kxtsoo.axBlockLimiter.database.impl;

import hu.kxtsoo.axBlockLimiter.AxBlockLimiter;
import hu.kxtsoo.axBlockLimiter.database.DatabaseInterface;
import hu.kxtsoo.axBlockLimiter.util.ConfigUtil;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.h2.jdbc.JdbcConnection;

import java.io.File;
import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class H2 implements DatabaseInterface {
    private final JavaPlugin plugin;
    private Connection connection;
    private final ConfigUtil configUtil;

    public H2(JavaPlugin plugin, ConfigUtil configUtil) {
        this.plugin = plugin;
        this.configUtil = configUtil;
    }

    @Override
    public void initialize() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            connection = new JdbcConnection("jdbc:h2:./" + AxBlockLimiter.getInstance().getDataFolder() + "/data;mode=MySQL", new Properties(), null, null, false);
            connection.setAutoCommit(true);

            createTables();
        } catch (SQLException e) {
            throw new RuntimeException("Could not connect to the H2 database", e);
        }
    }

    @Override
    public void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String createChunkBlockCountsTable = "CREATE TABLE IF NOT EXISTS axblocklimiter_block_counts (" +
                    "chunk_x INT NOT NULL, " +
                    "chunk_z INT NOT NULL, " +
                    "block_type VARCHAR(64) NOT NULL, " +
                    "count INT NOT NULL, " +
                    "PRIMARY KEY (chunk_x, chunk_z, block_type))";
            stmt.execute(createChunkBlockCountsTable);
        }
    }

    @Override
    public void incrementChunkBlockCount(int chunkX, int chunkZ, Material blockType) throws SQLException {
        String query = "MERGE INTO axblocklimiter_block_counts (chunk_x, chunk_z, block_type, count) KEY (chunk_x, chunk_z, block_type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, chunkX);
            stmt.setInt(2, chunkZ);
            stmt.setString(3, blockType.toString());
            stmt.setInt(4, getChunkBlockCount(chunkX, chunkZ, blockType) + 1);
            stmt.executeUpdate();
        }
    }

    @Override
    public void decrementChunkBlockCount(int chunkX, int chunkZ, Material blockType) throws SQLException {
        String query = "UPDATE axblocklimiter_block_counts SET count = count - 1 WHERE chunk_x = ? AND chunk_z = ? AND block_type = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, chunkX);
            stmt.setInt(2, chunkZ);
            stmt.setString(3, blockType.toString());
            stmt.executeUpdate();
        }
    }

    @Override
    public int getChunkBlockCount(int chunkX, int chunkZ, Material blockType) throws SQLException {
        String query = "SELECT count FROM axblocklimiter_block_counts WHERE chunk_x = ? AND chunk_z = ? AND block_type = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, chunkX);
            stmt.setInt(2, chunkZ);
            stmt.setString(3, blockType.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not close the H2 database connection", e);
        }
    }
}
