package gigaherz.inventoryspam.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gigaherz.inventoryspam.InventorySpam;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class Config
{
    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;
    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    static class ClientConfig
    {
        private static final Set<String> DRAW_POSITIONS = Arrays.stream(DrawPosition.values()).map(Enum::toString).collect(Collectors.toSet());

        public ForgeConfigSpec.BooleanValue showItemAdditions;
        public ForgeConfigSpec.BooleanValue showItemRemovals;
        public ForgeConfigSpec.BooleanValue drawIcon;
        public ForgeConfigSpec.BooleanValue drawName;
        public ForgeConfigSpec.ConfigValue<String> drawPosition;
        public ForgeConfigSpec.IntValue drawOffsetHorizontal;
        public ForgeConfigSpec.IntValue drawOffsetVertical;
        public ForgeConfigSpec.DoubleValue drawScale;
        public ForgeConfigSpec.DoubleValue iconScale;
        public ForgeConfigSpec.IntValue softLimit;
        public ForgeConfigSpec.IntValue fadeLimit;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> ignoreItems;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> ignoreSubitemChanges;

        public ClientConfig(ForgeConfigSpec.Builder builder)
        {
            builder.push("general");
            ignoreItems = builder
                    .comment("TileEntities to allow regardless of the blacklist")
                    .translation("text.inventoryspam.config.ignore_items")
                    .defineList("ignore_items", Lists.newArrayList(), o -> o instanceof String);
            ignoreSubitemChanges = builder
                    .comment("TileEntities to allow regardless of the blacklist")
                    .translation("text.inventoryspam.config.ignore_items")
                    .defineList("ignore_subitem_changes", Lists.newArrayList(), o -> o instanceof String);
            showItemAdditions = builder
                    .comment("TileEntities to disallow (whitelist takes precedence)")
                    .translation("text.inventoryspam.config.show_item_additions")
                    .define("show_item_additions", true);
            showItemRemovals = builder
                    .comment("TileEntities to disallow (whitelist takes precedence)")
                    .translation("text.inventoryspam.config.show_item_removals")
                    .define("show_item_removals", true);
            drawIcon = builder
                    .comment("TileEntities to disallow (whitelist takes precedence)")
                    .translation("text.inventoryspam.config.draw_icon")
                    .define("draw_icon", true);
            drawName = builder
                    .comment("TileEntities to disallow (whitelist takes precedence)")
                    .translation("text.inventoryspam.config.draw_name")
                    .define("draw_name", true);
            drawPosition = builder
                    .comment("TileEntities to disallow (whitelist takes precedence)")
                    .translation("text.inventoryspam.config.draw_position")
                    .define("draw_position", DrawPosition.BottomRight::toString, (e) -> e instanceof String && DRAW_POSITIONS.contains(e));
            drawOffsetHorizontal = builder
                    .comment("TileEntities to disallow (whitelist takes precedence)")
                    .translation("text.inventoryspam.config.draw_offset_horizontal")
                    .defineInRange("draw_offset_horizontal", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            drawOffsetVertical = builder
                    .comment("TileEntities to disallow (whitelist takes precedence)")
                    .translation("text.inventoryspam.config.draw_offset_vertical")
                    .defineInRange("draw_offset_vertical", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            drawScale = builder
                    .comment("TileEntities to disallow (whitelist takes precedence)")
                    .translation("text.inventoryspam.config.draw_scale")
                    .defineInRange("draw_scale", 1.0, 0.1, 2.0);
            iconScale = builder
                    .comment("TileEntities to disallow (whitelist takes precedence)")
                    .translation("text.inventoryspam.config.icon_scale")
                    .defineInRange("icon_scale", 0.6, 0.1, 2.0);
            softLimit = builder
                    .comment("TileEntities to disallow (whitelist takes precedence)")
                    .translation("text.inventoryspam.config.soft_limit")
                    .defineInRange("soft_limit", 0, 1, 30);
            fadeLimit = builder
                    .comment("TileEntities to disallow (whitelist takes precedence)")
                    .translation("text.inventoryspam.config.fade_limit")
                    .defineInRange("fade_limit", 0, 0, 15);
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
    public static double drawScale = 1.0;
    public static double iconScale = 0.6;
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

    public static void reload()
    {
        showItemAdditions = CLIENT.showItemAdditions.get();
        showItemRemovals = CLIENT.showItemRemovals.get();

        drawIcon = CLIENT.drawIcon.get();
        drawName = CLIENT.drawName.get();

        drawOffsetHorizontal = CLIENT.drawOffsetHorizontal.get();
        drawOffsetVertical = CLIENT.drawOffsetVertical.get();

        drawScale = Math.pow(10, CLIENT.drawScale.get());
        iconScale = Math.pow(10, CLIENT.iconScale.get());

        softLimit = CLIENT.softLimit.get();
        fadeLimit = CLIENT.fadeLimit.get();

        drawPosition = DrawPosition.valueOf(CLIENT.drawPosition.get());

        ignoreItems = Sets.newHashSet(CLIENT.ignoreItems.get());
        ignoreSubitemChanges = Sets.newHashSet(CLIENT.ignoreSubitemChanges.get());
    }

    @SubscribeEvent
    public static void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (InventorySpam.MODID.equals(event.getModID()))
        {
            reload();
        }
    }
}
