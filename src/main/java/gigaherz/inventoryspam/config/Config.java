package gigaherz.inventoryspam.config;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class Config
{
    private static Configuration config;
    private static ConfigCategory general;
    private static Property showItemAdditionsProperty;
    private static Property showItemRemovalsProperty;
    private static Property drawIconProperty;
    private static Property drawNameProperty;
    private static Property drawTimesProperty;
    private static Property drawOffsetVerticalProperty;
    private static Property drawOffsetHorizontalProperty;

    public static boolean showItemAdditions;
    public static boolean showItemRemovals;
    public static boolean drawIcon;
    public static boolean drawName;
    public static boolean drawTimes;
    public static int drawOffsetHorizontal;
    public static int drawOffsetVertical;

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
        drawNameProperty = config.get("General", "DrawName", true);
        drawTimesProperty = config.get("General", "DrawX", true);
        drawOffsetHorizontalProperty = config.get("General", "DrawOffsetHorizontal", 0);
        drawOffsetVerticalProperty = config.get("General", "DrawOffsetVertical", 0);

        drawOffsetHorizontalProperty.setMinValue(0);
        drawOffsetVerticalProperty.setMinValue(0);

        drawOffsetHorizontalProperty.setMaxValue(256);
        drawOffsetVerticalProperty.setMaxValue(256);

        reload();

        if (!showItemAdditionsProperty.wasRead()
                || !showItemRemovalsProperty.wasRead()
                || !drawIconProperty.wasRead()
                || !drawNameProperty.wasRead()
                || !drawTimesProperty.wasRead()
                || !drawOffsetHorizontalProperty.wasRead()
                || !drawOffsetHorizontalProperty.wasRead()
                )
            config.save();
    }

    public static void reload()
    {
        showItemAdditions = showItemAdditionsProperty.getBoolean();
        showItemRemovals = showItemRemovalsProperty.getBoolean();

        drawIcon = drawIconProperty.getBoolean();
        drawName = drawNameProperty.getBoolean();
        drawTimes = drawTimesProperty.getBoolean();

        drawOffsetHorizontal = drawOffsetHorizontalProperty.getInt();
        drawOffsetVertical = drawOffsetVerticalProperty.getInt();
    }
}
