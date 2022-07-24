package gigaherz.inventoryspam;

import gigaherz.inventoryspam.config.ConfigData;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;

@Mod(InventorySpam.MODID)
public class InventorySpam
{
    public static final String MODID = "inventoryspam";

    public static InventorySpam instance;

    public InventorySpam()
    {
        instance = this;

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::bakeConfigs);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigData.CLIENT_SPEC);

        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        //ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> ConfigGuiFactory.createConfigGui(mc, screen));
    }

    public void bakeConfigs(ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == ConfigData.CLIENT_SPEC)
            ConfigData.bake();
    }
}
