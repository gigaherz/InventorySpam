package gigaherz.inventoryspam;

import gigaherz.inventoryspam.config.Config;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.logging.Logger;

@Mod.EventBusSubscriber
@Mod(InventorySpam.MODID)
public class InventorySpam
{
    public static final String MODID = "inventoryspam";
    public static final String VERSION = "@VERSION@";

    // The instance of your mod that Forge uses.
    public static InventorySpam instance;

    public static final java.util.logging.Logger LOGGER = Logger.getLogger(MODID);

    public InventorySpam()
    {
        instance = this;

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

        //ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> ConfigGuiFactory.createConfigGui(mc, screen));
    }

    public void serverConfig(ModConfig.ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == Config.CLIENT_SPEC)
            Config.reload();
    }

    private void preInit(FMLCommonSetupEvent event)
    {
        //Config.init(event.getSuggestedConfigurationFile());
    }

    private void clientInit(FMLClientSetupEvent event)
    {
        ScrollingOverlay.init();
    }
}
