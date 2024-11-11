package gigaherz.inventoryspam.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigData
{
    public static final ClientConfig CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;
    static {
        final Pair<ClientConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    private static class ClientConfig
    {
        private static final Set<String> DRAW_POSITIONS = Arrays.stream(DrawPosition.values()).map(Enum::toString).collect(Collectors.toSet());

        ModConfigSpec.BooleanValue showItemAdditions;
        ModConfigSpec.BooleanValue showItemRemovals;
        ModConfigSpec.BooleanValue drawIcon;
        ModConfigSpec.BooleanValue drawName;
        ModConfigSpec.EnumValue<DrawPosition> drawPosition;
        ModConfigSpec.IntValue drawOffsetHorizontal;
        ModConfigSpec.IntValue drawOffsetVertical;
        ModConfigSpec.DoubleValue drawScale;
        ModConfigSpec.DoubleValue iconScale;
        ModConfigSpec.IntValue softLimit;
        ModConfigSpec.IntValue fadeLimit;
        ModConfigSpec.ConfigValue<List<? extends String>> ignoreItems;
        ModConfigSpec.ConfigValue<List<? extends String>> ignoreSubitemChanges;

        ClientConfig(ModConfigSpec.Builder builder)
        {
            builder.push("general");
            ignoreItems = builder
                    .comment("Items to ignore when finding changes")
                    .translation("text.inventoryspam.config.ignore_items")
                    .defineList("ignore_items", Lists.newArrayList(), o -> o instanceof String);
            ignoreSubitemChanges = builder
                    .comment("Items for which to ignore NBT changes")
                    .translation("text.inventoryspam.config.ignore_subitems")
                    .defineList("ignore_subitem_changes", Lists.newArrayList(), o -> o instanceof String);
            showItemAdditions = builder
                    .comment("Show gained items in the overlay")
                    .translation("text.inventoryspam.config.show_item_additions")
                    .define("show_item_additions", true);
            showItemRemovals = builder
                    .comment("Show lost items in the overlay")
                    .translation("text.inventoryspam.config.show_item_removals")
                    .define("show_item_removals", true);
            drawIcon = builder
                    .comment("Draw item icon in the overlay")
                    .translation("text.inventoryspam.config.draw_icon")
                    .define("draw_icon", true);
            drawName = builder
                    .comment("Draw item name in the overlay")
                    .translation("text.inventoryspam.config.draw_name")
                    .define("draw_name", true);
            drawPosition = builder
                    .comment("Which corner/edge of the screen to base the overlay position on")
                    .translation("text.inventoryspam.config.draw_position")
                    .defineEnum("draw_position", DrawPosition.BottomRight, DrawPosition.values());
            drawOffsetHorizontal = builder
                    .comment("Horizontal offset from the chosen position to draw the overlay on")
                    .translation("text.inventoryspam.config.draw_offset_horizontal")
                    .defineInRange("draw_offset_horizontal", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            drawOffsetVertical = builder
                    .comment("Vertical offset from the chosen position to draw the overlay on")
                    .translation("text.inventoryspam.config.draw_offset_vertical")
                    .defineInRange("draw_offset_vertical", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            drawScale = builder
                    .comment("Size of the overlay relative to the GUI scale")
                    .translation("text.inventoryspam.config.draw_scale")
                    .defineInRange("draw_scale", 1.0, 0.001, 1000.0);
            iconScale = builder
                    .comment("Size of the icons relative to the overlay size")
                    .translation("text.inventoryspam.config.icon_scale")
                    .defineInRange("icon_scale", 0.6, 0.001, 1000.0);
            softLimit = builder
                    .comment("Number items to show in the overlay before older items start to get forcefully faded")
                    .translation("text.inventoryspam.config.soft_limit")
                    .defineInRange("soft_limit", 10, 1, 30);
            fadeLimit = builder
                    .comment("Number of items to show progressively faded, when the soft limit is reached")
                    .translation("text.inventoryspam.config.fade_limit")
                    .defineInRange("fade_limit", 5, 0, 15);
            builder.pop();
        }
    }

    public static boolean showItemAdditions = true;
    public static boolean showItemRemovals = true;
    public static boolean drawIcon = true;
    public static boolean drawName = true;
    public static int drawOffsetHorizontal;
    public static int drawOffsetVertical;
    public static DrawPosition drawPosition = DrawPosition.BottomRight;
    public static float drawScale = 1.0f;
    public static float iconScale = 0.6f;
    public static int softLimit = 10;
    public static int fadeLimit = 5;

    public static Set<String> ignoreItems = Sets.newHashSet();
    public static Set<String> ignoreSubitemChanges = Sets.newHashSet();

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

    public static void refreshClient()
    {
        showItemAdditions = CLIENT.showItemAdditions.get();
        showItemRemovals = CLIENT.showItemRemovals.get();

        drawIcon = CLIENT.drawIcon.get();
        drawName = CLIENT.drawName.get();

        drawOffsetHorizontal = CLIENT.drawOffsetHorizontal.get();
        drawOffsetVertical = CLIENT.drawOffsetVertical.get();

        drawScale = CLIENT.drawScale.get().floatValue();
        iconScale = CLIENT.iconScale.get().floatValue();

        softLimit = CLIENT.softLimit.get();
        fadeLimit = CLIENT.fadeLimit.get();

        drawPosition = CLIENT.drawPosition.get();

        ignoreItems = Sets.newHashSet(CLIENT.ignoreItems.get());
        ignoreSubitemChanges = Sets.newHashSet(CLIENT.ignoreSubitemChanges.get());
    }
}
