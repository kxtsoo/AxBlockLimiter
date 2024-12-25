package hu.kxtsoo.axBlockLimiter.hook;

import hu.kxtsoo.axBlockLimiter.AxBlockLimiter;
import hu.kxtsoo.axBlockLimiter.hook.impl.region.KingdomsXHook;
import hu.kxtsoo.axBlockLimiter.util.ConfigUtil;

public class HookManager {

    private final AxBlockLimiter plugin;
    private final ConfigUtil configUtil;
    private RegionHook regionHook;

    public HookManager(AxBlockLimiter plugin, ConfigUtil configUtil) {
        this.plugin = plugin;
        this.configUtil = configUtil;
    }

    public void registerHooks() {
        String selectedRegionPlugin = configUtil.getHooks().getString("hooks.settings.region-plugin", "").toLowerCase();

        if (selectedRegionPlugin.equals("kingdomsx") &&
                configUtil.getHooks().getBoolean("hooks.register.KingdomsX", true)) {

            regionHook = new KingdomsXHook();
            plugin.getLogger().info("\u001B[32m[Hook] KingdomsX successfully enabled.\u001B[0m");

        }
    }

    public RegionHook getRegionHook() {
        return regionHook;
    }
}