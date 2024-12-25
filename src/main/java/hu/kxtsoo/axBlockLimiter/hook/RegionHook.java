package hu.kxtsoo.axBlockLimiter.hook;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface RegionHook {
    boolean isInProtectedRegion(Location location);
    boolean canBuild(Player player, Location location);
}
