package gigaherz.inventoryspam;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import gigaherz.inventoryspam.config.ConfigData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

@EventBusSubscriber(value= Dist.CLIENT, modid=InventorySpam.MODID, bus= EventBusSubscriber.Bus.MOD)
public class ScrollingOverlay implements LayeredDraw.Layer
{

    public static final ResourceLocation OVERLAY_ID = ResourceLocation.fromNamespaceAndPath("inventoryspam", "inventoryspam.overlay");

    @SubscribeEvent
    public static void registerOverlay(RegisterGuiLayersEvent event)
    {
        event.registerAbove(VanillaGuiLayers.CHAT, OVERLAY_ID, new ScrollingOverlay());
    }

    private static final int TTL = 240;
    private static final int FADE = 40;

    private int hard_limit;

    private ResourceKey<Level> dim;
    private int dimLoadTicks;
    private ItemStack[] previous;
    private Player playerEntity;

    private ItemStack previousInCursor = ItemStack.EMPTY;
    private final List<ChangeInfo> changeEntries = Lists.newArrayList();

    private final Minecraft mc = Minecraft.getInstance();

    public ScrollingOverlay()
    {
        NeoForge.EVENT_BUS.addListener(this::clientLogOut);
        NeoForge.EVENT_BUS.addListener(this::clientTick);
    }

    public void clientLogOut(ClientPlayerNetworkEvent.LoggingOut event)
    {
        changeEntries.clear();
    }

    public void clientTick(ClientTickEvent.Post event)
    {
        tick();
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker partialTicks)
    {
        if (!ConfigData.showItemAdditions && !ConfigData.showItemRemovals)
            return;

        var width = (int) (graphics.guiWidth() / ConfigData.drawScale);
        var height = (int) (graphics.guiHeight() / ConfigData.drawScale);

        Font font = mc.font;

        int iconSize = (int) (16 * ConfigData.iconScale);
        int rightMargin = ConfigData.drawIcon ? (2 + iconSize) : 0;
        int topMargin1 = 2 + (ConfigData.drawIcon ? Math.max(0, (iconSize - font.lineHeight) / 2) : 0);
        int topMargin2 = 1 + Math.max(0, -(iconSize - font.lineHeight) / 2);

        int lineHeight = font.lineHeight;
        if (ConfigData.drawIcon)
            lineHeight = Math.max(2 + iconSize, lineHeight);

        hard_limit = height / lineHeight;

        List<Triple<ChangeInfo, Component, Integer>> labels = Lists.newArrayList();

        int rectWidth;
        int number;

        synchronized (changeEntries)
        {
            if (changeEntries.size() == 0)
                return;

            rectWidth = computeLabel(labels, font);

            number = labels.size();
            if (number == 0)
                return;
        }

        var poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.scale(ConfigData.drawScale, ConfigData.drawScale, 1);

        rectWidth += rightMargin;

        int rectHeight = lineHeight * number;

        int x, y;
        int align;
        switch (ConfigData.drawPosition)
        {
            default -> { // incl BottomRight
                x = width - 2 - rectWidth - ConfigData.drawOffsetHorizontal;
                y = height - 2 - rectHeight - ConfigData.drawOffsetVertical;
                align = 1;
            }
            case Bottom -> {
                x = (width - rectWidth) / 2 - 2 + ConfigData.drawOffsetHorizontal;
                y = height - 2 - rectHeight - ConfigData.drawOffsetVertical;
                align = 0;
            }
            case BottomLeft -> {
                x = 2 + ConfigData.drawOffsetHorizontal;
                y = height - 2 - rectHeight - ConfigData.drawOffsetVertical;
                align = -1;
            }
            case Left -> {
                x = 2 + ConfigData.drawOffsetHorizontal;
                y = (height - rectHeight) / 2 - 2 + ConfigData.drawOffsetVertical;
                align = -1;
            }
            case TopLeft -> {
                x = 2 + ConfigData.drawOffsetHorizontal;
                y = 2 + ConfigData.drawOffsetVertical;
                align = -1;
            }
            case Top -> {
                x = (width - rectWidth) / 2 - 2 + ConfigData.drawOffsetHorizontal;
                y = 2 + ConfigData.drawOffsetVertical;
                align = 0;
            }
            case TopRight -> {
                x = width - 2 - rectWidth - ConfigData.drawOffsetHorizontal;
                y = 2 + ConfigData.drawOffsetVertical;
                align = 1;
            }
            case Right -> {
                x = width - 2 - rectWidth - ConfigData.drawOffsetHorizontal;
                y = (height - rectHeight) / 2 - 2 + ConfigData.drawOffsetVertical;
                align = 1;
            }
            case Center -> {
                x = (width - rectWidth) / 2 - 2 + ConfigData.drawOffsetHorizontal;
                y = (height - rectHeight) / 2 - 2 + ConfigData.drawOffsetVertical;
                align = 0;
            }
        }

        int backgroundColor = ((int) Mth.clamp(mc.options.textBackgroundOpacity().get() * 255, 0, 255)) << 24;
        graphics.fill(x - 2, y - 2, x + rectWidth + 4, y + rectHeight + 4, backgroundColor);

        for (Triple<ChangeInfo, Component, Integer> e : labels)
        {
            ChangeInfo change = e.getLeft();
            Component label = e.getMiddle();
            int fade = e.getRight();

            int w = font.width(label);

            int forcedFade = ConfigData.fadeLimit > 0 ? (fade * 255 / (ConfigData.fadeLimit + 2)) : 255;
            int ttlFade = change.ttl * 255 / FADE;
            int alpha = Math.min(255, Math.min(forcedFade, ttlFade));
            int color = alpha << 24 | (change.mode == ChangeMode.Obtained ? 0x7FFF7F : 0xFF5F5F);

            int leftMargin = switch (align)
            {
                case -1 -> 2;
                case 0 -> (rectWidth - w - rightMargin) / 2;
                case 1 -> rectWidth - w - rightMargin;
                default -> 0;
            };

            RenderSystem.enableBlend();
            graphics.drawString(font, label, x + leftMargin, y + topMargin1, color);

            if (ConfigData.drawIcon)
            {
                poseStack.pushPose();
                poseStack.translate(x + 2 + w + leftMargin, y + topMargin2, 0);
                poseStack.scale(ConfigData.iconScale, ConfigData.iconScale, 1);
                graphics.renderItem(change.item.stack, 0, 0);
                graphics.renderItemDecorations(font, change.item.stack, 0, 0, null);
                poseStack.popPose();
            }

            y += lineHeight;
        }

        poseStack.popPose();
    }

    private int computeLabel(List<Triple<ChangeInfo, Component, Integer>> computedStrings, Font font)
    {
        int rectWidth = 0;
        int itemsToShow = Math.min(Math.min(hard_limit, ConfigData.softLimit + ConfigData.fadeLimit), changeEntries.size());
        int offset = Math.max(0, changeEntries.size() - itemsToShow);
        int fadeOffset = changeEntries.size() - ConfigData.softLimit - ConfigData.fadeLimit;

        for (int i = offset; i < changeEntries.size(); i++)
        {
            ChangeInfo change = changeEntries.get(i);
            var label = getChangeLabel(change);

            int w = font.width(label);

            rectWidth = Math.max(rectWidth, w);

            computedStrings.add(Triple.of(change, label, Math.min(ConfigData.fadeLimit + 2, 1 + i - fadeOffset)));
        }
        return rectWidth;
    }

    private Component getChangeLabel(ChangeInfo change)
    {
        String mode = change.mode == ChangeMode.Obtained ? "+" : "-";
        var label = Component.literal(String.format("%s%d", mode, change.count));
        if (ConfigData.drawName)
        {
            label = label.append(Component.literal(" "));

            var name = change.item.stack.getHoverName();
            if (change.item.stack.has(DataComponents.CUSTOM_NAME))
                name = name.copy().withStyle(style -> style.withItalic(true));
            label = label.append(name);
        }
        return label;
    }

    private void tick()
    {
        if (!ConfigData.showItemAdditions && !ConfigData.showItemRemovals)
            return;

        LocalPlayer player = mc.player;

        if (player == null)
            return;

        if (player != playerEntity || player.inventoryMenu != PlayerContainerHooks.getOriginalContainer())
        {
            PlayerContainerHooks.setTarget(player.inventoryMenu, () ->
            {
                previous = null;
                dimLoadTicks = 0;
            });
            playerEntity = player;
            previous = null;
        }

        if (player.level().dimension() != dim)
        {
            previous = null;
            dimLoadTicks = 50;
            dim = player.level().dimension();
        }

        if (dimLoadTicks > 0)
        {
            previous = null;
            dimLoadTicks--;
            return;
        }

        synchronized (changeEntries)
        {
            changeEntries.forEach(e -> e.ttl--);
            while (changeEntries.size() > hard_limit)
            {
                changeEntries.remove(0);
            }
            changeEntries.removeIf((e) -> e.ttl <= 0 || e.count == 0);
        }

        Inventory inventory = player.getInventory();
        if (previous == null ||
                // I don't think this can happen but eh.
                previous.length != inventory.getContainerSize())
        {
            previous = new ItemStack[inventory.getContainerSize()];
            for (int i = 0; i < inventory.getContainerSize(); i++)
            {
                previous[i] = safeCopy(inventory.getItem(i));
            }

            var currentMenu = player.containerMenu;
            previousInCursor = currentMenu.getCarried();

            return;
        }


        final List<Pair<ItemStack, ItemStack>> changes = Lists.newArrayList();
        for (int i = 0; i < inventory.getContainerSize(); i++)
        {
            ItemStack stack = inventory.getItem(i);
            ItemStack old = previous[i];
            if (isChangeMeaningful(old, stack))
            {
                changes.add(Pair.of(old, stack));
            }
            previous[i] = stack.copy();
        }

        var currentMenu = player.containerMenu;
        ItemStack stackInCursor = currentMenu.getCarried();
        if (isChangeMeaningful(stackInCursor, previousInCursor))
            changes.add(Pair.of(previousInCursor, stackInCursor));
        previousInCursor = stackInCursor.copy();

        if (changes.size() == 0)
            return;

        final List<ChangeInfo> changeList = Lists.newArrayList();
        changes.forEach((change) ->
        {
            ItemStack left = change.getLeft();
            boolean leftEmpty = left.getCount() <= 0;

            ItemStack right = change.getRight();
            boolean rightEmpty = right.getCount() <= 0;

            if (areSameishItem(left, right))
            {
                if (!isBlacklisted(left))
                {
                    int difference = right.getCount() - left.getCount();
                    if (difference > 0)
                        obtainedItem(changeList, left, difference);
                    else if (difference < 0)
                        lostItem(changeList, left, -difference);
                }
            }
            else
            {
                if (!leftEmpty && !isBlacklisted(left))
                {
                    lostItem(changeList, left, left.getCount());
                }
                if (!rightEmpty && !isBlacklisted(right))
                {
                    obtainedItem(changeList, right, right.getCount());
                }
            }
        });

        changeList.removeIf((e) -> e.count == 0);

        if (changeList.size() > 0)
        {
            synchronized (changeEntries)
            {
                for (ChangeInfo info : changeList)
                {
                    if (info.count == 0)
                        continue;
                    accumulate(changeEntries, info.item.stack, info.mode, info.count, false);
                }
            }
        }
    }

    private boolean isBlacklisted(ItemStack left)
    {
        var name = BuiltInRegistries.ITEM.getKey(left.getItem());
        if(name == null)
            return true;
        return ConfigData.ignoreItems.contains(name.toString());
    }

    private boolean isChangeMeaningful(ItemStack a, ItemStack b)
    {
        if (a.getCount() != b.getCount())
            return true;

        if (a == b || isStackEmpty(a) && isStackEmpty(b))
            return false;

        var name = BuiltInRegistries.ITEM.getKey(a.getItem());
        if (a.getItem() == b.getItem() && name != null && ConfigData.ignoreSubitemChanges.contains(name.toString()))
        {
            // If we are ignoring subitem changes, consider them the same.
            return false;
        }

        return !ItemStack.isSameItem(a, b);
    }

    private static boolean areLooselyTheSame(ItemStack a, ItemStack b)
    {
        return a == b
                || isStackEmpty(a) && isStackEmpty(b)
                || ItemStack.isSameItem(a, b);
    }

    private static boolean areSameishItem(ItemStack a, ItemStack b)
    {
        return a == b
                || (isStackEmpty(a) && isStackEmpty(b))
                || ItemStack.isSameItemSameComponents(a, b);
    }

    private static boolean isStackEmpty(ItemStack stack)
    {
        return stack.getCount() <= 0;
    }

    private static ItemStack safeCopy(ItemStack stack)
    {
        return stack.copy();
    }

    private void obtainedItem(List<ChangeInfo> changeList, ItemStack item, int added)
    {
        if (added <= 0 || !ConfigData.showItemAdditions)
            return;

        accumulate(changeList, item, ChangeMode.Obtained, added, true);
    }

    private void lostItem(List<ChangeInfo> changeList, ItemStack item, int removed)
    {
        if (removed <= 0 || !ConfigData.showItemRemovals)
            return;

        accumulate(changeList, item, ChangeMode.Lost, removed, true);
    }

    private void accumulate(List<ChangeInfo> changeList, ItemStack stack, ChangeMode mode, int count, boolean isLocal)
    {
        if (stack.getCount() <= 0)
            return;

        final ComparableItem name = new ComparableItem(stack);
        ChangeInfo info = isLocal
                ? changeList.stream().filter(e -> e.item.equals(name)).findFirst().orElse(null)
                : changeList.stream().filter(e -> e.item.equals(name) && e.mode == mode).findFirst().orElse(null);
        if (info == null)
        {
            info = new ChangeInfo(name, mode, count, TTL);
            changeList.add(info);
            return;
        }

        if (info.mode != mode)
        {
            count = -count;
        }

        info.count += count;
        info.ttl = TTL;

        if (info.count < 0)
        {
            info.count = -info.count;
            info.mode = info.mode == ChangeMode.Lost ? ChangeMode.Obtained : ChangeMode.Lost;
        }
    }

    private static class ChangeInfo
    {
        final ComparableItem item;
        ChangeMode mode;
        int count;
        int ttl;

        ChangeInfo(ComparableItem item, ChangeMode mode, int count, int ttl)
        {
            this.item = item;
            this.mode = mode;
            this.count = count;
            this.ttl = ttl;
        }
    }

    private enum ChangeMode
    {
        Obtained, Lost
    }

    private record ComparableItem(ItemStack stack)
    {
        public ComparableItem(ItemStack stack)
        {
            this.stack = stack.copy();
            this.stack.setCount(1);
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof ComparableItem other
                    && areSameishItem(other.stack, this.stack);
        }

        @Override
        public int hashCode()
        {
            return stack.getItem().hashCode();
        }
    }
}
