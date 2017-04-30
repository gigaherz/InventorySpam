package gigaherz.inventoryspam.config;

import gigaherz.inventoryspam.InventorySpam;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.Arrays;

@Mod.EventBusSubscriber
public class Config
{
    private static Configuration config;
    private static ConfigCategory general;
    private static Property showItemAdditionsProperty;
    private static Property showItemRemovalsProperty;
    private static Property drawIconProperty;
    private static Property drawOffsetVerticalProperty;
    private static Property drawOffsetHorizontalProperty;
    private static Property drawPositionProperty;

    public static boolean showItemAdditions;
    public static boolean showItemRemovals;
    public static boolean drawIcon;
    public static int drawOffsetHorizontal;
    public static int drawOffsetVertical;
    public static DrawPosition drawPosition;

    public enum DrawPosition
    {
        BottomRight,
        Bottom,
        BottomLeft,
        Left,
        TopLeft,
        Top,
        TopRight,
        Right,
        Center
    }

    public static ConfigCategory getGeneralCategory() { return general; }

    public static void init(File configurationFile)
    {
        config = new Configuration(configurationFile);
        config.load();

        general = config.getCategory("General");
        general.setComment("General settings");

        showItemAdditionsProperty = config.get("General", "ShowItemAdditions", true);
        showItemRemovalsProperty = config.get("General", "ShowItemRemovals", true);

        drawIconProperty = config.get("General", "DrawIcon", true);
        drawOffsetHorizontalProperty = config.get("General", "DrawOffsetHorizontal", 0);
        drawOffsetVerticalProperty = config.get("General", "DrawOffsetVertical", 18);

        drawOffsetHorizontalProperty.setMinValue(0);
        drawOffsetVerticalProperty.setMinValue(0);

        drawOffsetHorizontalProperty.setMaxValue(256);
        drawOffsetVerticalProperty.setMaxValue(256);

        drawPositionProperty = config.get("General", "SnapPosition", DrawPosition.BottomRight.toString());
        drawPositionProperty.setValidValues(Arrays.stream(DrawPosition.values())
                .map(DrawPosition::toString)
                .toArray(String[]::new));
        drawPositionProperty.setComment("Edge/corner of the screen to use as the base location:\n" +
                "  BottomRight\n" +
                "  Bottom\n" +
                "  BottomLeft\n" +
                "  Left\n" +
                "  TopLeft\n" +
                "  Top\n" +
                "  TopRight\n" +
                "  Right\n" +
                "  Center");

        reload();

        if (config.hasChanged()
                || !showItemAdditionsProperty.wasRead()
                || !showItemRemovalsProperty.wasRead()
                || !drawIconProperty.wasRead()
                || !drawOffsetHorizontalProperty.wasRead()
                || !drawOffsetHorizontalProperty.wasRead()
                || !drawPositionProperty.wasRead()
                )
            config.save();
    }

    private static void reload()
    {
        showItemAdditions = showItemAdditionsProperty.getBoolean();
        showItemRemovals = showItemRemovalsProperty.getBoolean();

        drawIcon = drawIconProperty.getBoolean();

        drawOffsetHorizontal = drawOffsetHorizontalProperty.getInt();
        drawOffsetVertical = drawOffsetVerticalProperty.getInt();

        try
        {
            drawPosition = DrawPosition.valueOf(drawPositionProperty.getString());
        }
        catch(IllegalArgumentException ex)
        {
            drawPositionProperty.setToDefault();
            drawPosition = DrawPosition.valueOf(drawPositionProperty.getString());
        }
    }

    @SubscribeEvent
    public static void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (InventorySpam.MODID.equals(event.getModID()))
        {
            if(config.hasChanged())
                config.save();
            reload();
        }
    }
}
