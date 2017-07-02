package gigaherz.inventoryspam.config;

import gigaherz.inventoryspam.InventorySpam;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
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
    private static Property drawScaleProperty;
    private static Property iconScaleProperty;
    private static Property softLimitProperty;
    private static Property fadeLimitProperty;

    public static boolean showItemAdditions;
    public static boolean showItemRemovals;
    public static boolean drawIcon;
    public static int drawOffsetHorizontal;
    public static int drawOffsetVertical;
    public static DrawPosition drawPosition;
    public static double drawScale;
    public static double iconScale;
    public static int softLimit;
    public static int fadeLimit;

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

    public static ConfigCategory getGeneralCategory()
    {
        return general;
    }

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
        drawOffsetHorizontalProperty.setMinValue(0);
        drawOffsetHorizontalProperty.setMaxValue(256);
        drawOffsetHorizontalProperty.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);

        drawOffsetVerticalProperty = config.get("General", "DrawOffsetVertical", 18);
        drawOffsetVerticalProperty.setMinValue(0);
        drawOffsetVerticalProperty.setMaxValue(256);
        drawOffsetVerticalProperty.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);

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

        drawScaleProperty = config.get("General", "DrawScaleProperty", 0.0);
        drawScaleProperty.setMinValue(-2);
        drawScaleProperty.setMaxValue(2);
        drawScaleProperty.setComment("Exponential: -2 is 1%, 2 is 10000%(100x) -- Use the ingame config gui");
        drawScaleProperty.setConfigEntryClass(ExponentialNumberSliderEntry.class);

        iconScaleProperty = config.get("General", "IconScaleProperty", -0.2);
        iconScaleProperty.setMinValue(-2);
        iconScaleProperty.setMaxValue(2);
        iconScaleProperty.setComment("Exponential: -2 is 1%, 2 is 10000%(100x) -- Use the ingame config gui");
        iconScaleProperty.setConfigEntryClass(ExponentialNumberSliderEntry.class);

        softLimitProperty = config.get("General", "SoftLimit", 6);
        softLimitProperty.setMinValue(1);
        softLimitProperty.setComment("The maximum number of items in the queue before they start fading out artificially");

        fadeLimitProperty = config.get("General", "FadeLimit", 3);
        fadeLimitProperty.setMinValue(1);
        fadeLimitProperty.setComment("The number of items that will be faded out after the soft limit is reached");

        reload();

        if (config.hasChanged()
                || !showItemAdditionsProperty.wasRead()
                || !showItemRemovalsProperty.wasRead()
                || !drawIconProperty.wasRead()
                || !drawOffsetHorizontalProperty.wasRead()
                || !drawOffsetHorizontalProperty.wasRead()
                || !drawPositionProperty.wasRead()
                || !iconScaleProperty.wasRead()
                || !softLimitProperty.wasRead()
                || !fadeLimitProperty.wasRead()
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

        drawScale = Math.pow(10, drawScaleProperty.getDouble());
        iconScale = Math.pow(10, iconScaleProperty.getDouble());

        softLimit = softLimitProperty.getInt();
        fadeLimit = fadeLimitProperty.getInt();

        try
        {
            drawPosition = DrawPosition.valueOf(drawPositionProperty.getString());
        }
        catch (IllegalArgumentException ex)
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
            if (config.hasChanged())
                config.save();
            reload();
        }
    }
}
