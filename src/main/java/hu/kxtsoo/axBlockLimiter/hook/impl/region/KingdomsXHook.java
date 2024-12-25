package hu.kxtsoo.axBlockLimiter.hook.impl.region;

import hu.kxtsoo.axBlockLimiter.hook.RegionHook;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.kingdoms.constants.land.Land;

public class KingdomsXHook implements RegionHook {

    @Override
    public boolean isInProtectedRegion(Location location) {
        Land land = Land.getLand(location);
        return land != null && land.isClaimed();
    }

    @Override
    public boolean canBuild(Player player, Location location) {
        Land land = Land.getLand(location);
        if (land != null && land.isClaimed()) {
            return land.getKingdom().isMember(player.getUniqueId());
        }
        return true;
    }
}
