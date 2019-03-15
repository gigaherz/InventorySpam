package gigaherz.inventoryspam;

import gigaherz.inventoryspam.config.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber
@Mod(InventorySpam.MODID)
public class InventorySpam
{
    public static final String MODID = "inventoryspam";

    public static InventorySpam instance;

    public InventorySpam()
    {
        instance = this;

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::bakeConfigs);

            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

            //ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> ConfigGuiFactory.createConfigGui(mc, screen));
        });
    }

    public void bakeConfigs(ModConfig.ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == Config.CLIENT_SPEC)
            Config.bake();
    }

    private void clientInit(FMLClientSetupEvent event)
    {
        ScrollingOverlay.init();
    }
}
