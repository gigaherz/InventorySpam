package gigaherz.inventoryspam;

import gigaherz.inventoryspam.config.Config;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
@Mod(modid = InventorySpam.MODID, version = InventorySpam.VERSION,
        clientSideOnly = true,
        acceptableRemoteVersions = "*",
        canBeDeactivated = true,
        acceptedMinecraftVersions = "[1.11.0,1.13.0)",
        guiFactory = "gigaherz.inventoryspam.config.ConfigGuiFactory")
public class InventorySpam
{
    public static final String MODID = "inventoryspam";
    public static final String VERSION = "@VERSION@";

    // The instance of your mod that Forge uses.
    @Mod.Instance(InventorySpam.MODID)
    public static InventorySpam instance;

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        Config.init(event.getSuggestedConfigurationFile());
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ScrollingOverlay.init();
    }
}
