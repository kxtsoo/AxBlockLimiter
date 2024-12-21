package hu.kxtsoo.axBlockLimiter.database;

import org.bukkit.Material;

import java.sql.SQLException;

public interface DatabaseInterface {
    void initialize() throws SQLException;

    void createTables() throws SQLException;

    void incrementChunkBlockCount(int chunkX, int chunkZ, Material blockType) throws SQLException;
    void decrementChunkBlockCount(int chunkX, int chunkZ, Material blockType) throws SQLException;
    int getChunkBlockCount(int chunkX, int chunkZ, Material blockType) throws SQLException;

    void close() throws SQLException;
}
