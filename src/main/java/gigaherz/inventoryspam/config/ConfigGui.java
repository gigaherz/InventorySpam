package gigaherz.inventoryspam.config;

import com.google.common.collect.Lists;
import gigaherz.inventoryspam.InventorySpam;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;

public class ConfigGui extends GuiConfig
{
    public ConfigGui(GuiScreen parent)
    {
        super(parent, getConfigElements(), InventorySpam.MODID, false, false, getTitle());
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        Config.reload();
    }

    private static String getTitle()
    {
        return I18n.format("text." + InventorySpam.MODID + ".config");
    }

    private static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> configElements = Lists.newArrayList();

        ConfigElement parent = new ConfigElement(Config.getGeneralCategory());

        configElements.addAll(parent.getChildElements());

        return configElements;
    }
}
