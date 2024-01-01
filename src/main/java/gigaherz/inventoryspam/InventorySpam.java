package gigaherz.inventoryspam;

import gigaherz.inventoryspam.config.ConfigData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

@Mod(InventorySpam.MODID)
public class InventorySpam
{
    public static final String MODID = "inventoryspam";

    public static InventorySpam instance;

    public InventorySpam(IEventBus modEventBus)
    {
        instance = this;

        modEventBus.addListener(this::readConfigs);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigData.CLIENT_SPEC);
    }

    public void readConfigs(ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == ConfigData.CLIENT_SPEC)
            ConfigData.refreshClient();
    }
}
