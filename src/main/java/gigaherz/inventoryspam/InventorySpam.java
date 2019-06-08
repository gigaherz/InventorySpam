package gigaherz.inventoryspam;

import gigaherz.inventoryspam.config.ConfigData;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(InventorySpam.MODID)
public class InventorySpam
{
    public static final String MODID = "inventoryspam";

    public static InventorySpam instance;

    public InventorySpam()
    {
        instance = this;

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::bakeConfigs);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigData.CLIENT_SPEC);

        //ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> ConfigGuiFactory.createConfigGui(mc, screen));
    }

    public void bakeConfigs(ModConfig.ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == ConfigData.CLIENT_SPEC)
            ConfigData.bake();
    }

    private void clientInit(FMLClientSetupEvent event)
    {
        ScrollingOverlay.register();
    }
}
