package hu.kxtsoo.axBlockLimiter.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hu.kxtsoo.axBlockLimiter.database.DatabaseInterface;
import hu.kxtsoo.axBlockLimiter.util.ConfigUtil;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.UUID;

public class MySQL implements DatabaseInterface {
    private final ConfigUtil configUtil;
    private final JavaPlugin plugin;
    private HikariDataSource dataSource;

    public MySQL(ConfigUtil configUtil, JavaPlugin plugin) {
        this.configUtil = configUtil;
        this.plugin = plugin;
    }

    @Override
    public void initialize() throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();

        String host = configUtil.getConfig().getString("storage.host", "localhost");
        String port = configUtil.getConfig().getString("storage.port", "3306");
        String database = configUtil.getConfig().getString("storage.name", "database_name");
        String username = configUtil.getConfig().getString("storage.username", "root");
        String password = configUtil.getConfig().getString("storage.password", "");

        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        hikariConfig.setMaximumPoolSize(configUtil.getConfig().getInt("storage.pool.maximumPoolSize", 10));
        hikariConfig.setMinimumIdle(configUtil.getConfig().getInt("storage.pool.minimumIdle", 5));
        hikariConfig.setConnectionTimeout(configUtil.getConfig().getInt("storage.pool.connectionTimeout", 30000));
        hikariConfig.setMaxLifetime(configUtil.getConfig().getInt("storage.pool.maxLifetime", 1800000));
        hikariConfig.setIdleTimeout(configUtil.getConfig().getInt("storage.pool.idleTimeout", 600000));

        dataSource = new HikariDataSource(hikariConfig);
        createTables();
    }

    @Override
    public void createTables() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS axblocklimiter_players (" +
                    "uuid CHAR(36) PRIMARY KEY, " +
                    "ip_address VARCHAR(255))";
            stmt.execute(sql);
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
    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
