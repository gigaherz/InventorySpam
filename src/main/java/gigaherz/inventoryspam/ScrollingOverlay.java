package gigaherz.inventoryspam;

import com.google.common.collect.Lists;
import gigaherz.inventoryspam.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public class ScrollingOverlay extends GuiScreen
{
    public static void init()
    {
        MinecraftForge.EVENT_BUS.register(new ScrollingOverlay());
    }

    private static int TTL = 240;
    private static int FADE = 40;

    private RenderItem renderItem;
    private int id;
    private ItemStack[] previous;
    private ItemStack previousInCursor = ItemStack.EMPTY;

    private final List<ChangeInfo> changeEntries = Lists.newArrayList();

    public ScrollingOverlay()
    {
        renderItem = Minecraft.getMinecraft().getRenderItem();
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event)
    {
        if (!Config.showItemAdditions && !Config.showItemRemovals)
            return;

        if (event.getType() != RenderGameOverlayEvent.ElementType.CHAT)
            return;

        synchronized (changeEntries)
        {
            ScaledResolution resolution = event.getResolution();
            int width = resolution.getScaledWidth();
            int height = resolution.getScaledHeight();

            FontRenderer font = Minecraft.getMinecraft().fontRenderer;

            int number = changeEntries.size();

            if (number == 0)
                return;

            List<String> computedStrings = Lists.newArrayList();

            int rectWidth = 0;
            for (ChangeInfo change : changeEntries)
            {
                String s;

                String name = change.item.stack.getDisplayName();
                if (change.mode == 1)
                {
                    s = String.format("+%d %s", change.count, name);
                }
                else
                {
                    s = String.format("-%d %s", change.count, name);
                }

                int w = font.getStringWidth(s);
                rectWidth = Math.max(rectWidth, w);
                computedStrings.add(s);
            }

            int rightMargin = Config.drawIcon ? 18 : 0;
            int topMargin = Config.drawIcon ? 6 : 0;

            rectWidth += rightMargin;

            int lineHeight = font.FONT_HEIGHT;
            if (Config.drawIcon)
                lineHeight = 18;

            int rectHeight = lineHeight * number;

            int x, y;
            int align = 0;
            switch (Config.drawPosition)
            {
                default:
                case BottomRight:
                    x = width - 2 - rectWidth - Config.drawOffsetHorizontal;
                    y = height - 2 - rectHeight - Config.drawOffsetVertical;
                    align = 1;
                    break;
                case Bottom:
                    x = (width - rectWidth) / 2 - 2 + Config.drawOffsetHorizontal;
                    y = height - 2 - rectHeight - Config.drawOffsetVertical;
                    align = 0;
                    break;
                case BottomLeft:
                    x = 2 + Config.drawOffsetHorizontal;
                    y = height - 2 - rectHeight - Config.drawOffsetVertical;
                    align = -1;
                    break;
                case Left:
                    x = 2 + Config.drawOffsetHorizontal;
                    y = (height - rectHeight) / 2 - 2 + Config.drawOffsetVertical;
                    align = -1;
                    break;
                case TopLeft:
                    x = 2 + Config.drawOffsetHorizontal;
                    y = 2 + Config.drawOffsetVertical;
                    align = -1;
                    break;
                case Top:
                    x = (width - rectWidth) / 2 - 2 + Config.drawOffsetHorizontal;
                    y = 2 + Config.drawOffsetVertical;
                    align = 0;
                    break;
                case TopRight:
                    x = width - 2 - rectWidth - Config.drawOffsetHorizontal;
                    y = 2 + Config.drawOffsetVertical;
                    align = 1;
                    break;
                case Right:
                    x = width - 2 - rectWidth - Config.drawOffsetHorizontal;
                    y = (height - rectHeight) / 2 - 2 + Config.drawOffsetVertical;
                    align = 1;
                    break;
                case Center:
                    x = (width - rectWidth) / 2 - 2 + Config.drawOffsetHorizontal;
                    y = (height - rectHeight) / 2 - 2 + Config.drawOffsetVertical;
                    align = 0;
                    break;
            }

            drawRect(x - 2, y - 2, x + rectWidth + 4, y + rectHeight + 4, Integer.MIN_VALUE);

            for (int i = 0; i < changeEntries.size(); i++)
            {
                String s = computedStrings.get(i);
                int w = font.getStringWidth(s);

                ChangeInfo change = changeEntries.get(i);
                int alpha = Math.min(255, change.ttl * 255 / FADE);
                int color = alpha << 24 | (change.mode == 1 ? 0x7FFF7F : 0xFF5F5F);

                int leftMargin = 0;
                switch(align)
                {
                    case -1: leftMargin = 2; break;
                    case 0: leftMargin = (rectWidth - w - rightMargin)/2; break;
                    case 1: leftMargin = rectWidth - w - rightMargin; break;
                }

                GlStateManager.enableBlend();
                font.drawStringWithShadow(s, x + leftMargin, y + topMargin, color);

                if (Config.drawIcon)
                {
                    RenderHelper.enableGUIStandardItemLighting();
                    renderItem.renderItemAndEffectIntoGUI(change.item.stack, x + 2 + w + leftMargin, y + 1);
                    renderItem.renderItemOverlayIntoGUI(font, change.item.stack, x + 2 + w + leftMargin - rightMargin, y + 1, null);
                    RenderHelper.disableStandardItemLighting();
                }

                y += lineHeight;
            }
        }
    }

    @SubscribeEvent
    public void clientTick(TickEvent.PlayerTickEvent event)
    {
        if (!Config.showItemAdditions && !Config.showItemRemovals)
            return;

        EntityPlayer player = event.player;

        if (!player.world.isRemote)
            return;

        synchronized (changeEntries)
        {
            changeEntries.forEach((a) -> --a.ttl);
            changeEntries.removeIf((e) -> e.ttl <= 0 || e.count == 0);
        }

        if (previous == null || player.getEntityId() != id)
        {
            previous = new ItemStack[player.inventory.getSizeInventory()];
            for (int i = 0; i < player.inventory.getSizeInventory(); i++)
            {
                previous[i] = player.inventory.getStackInSlot(i);
            }
            previousInCursor = player.inventory.getItemStack();
            id = player.getEntityId();
            return;
        }

        // I don't htink this can happen but eh.
        if (previous.length != player.inventory.getSizeInventory())
        {
            previous = Arrays.copyOf(previous, player.inventory.getSizeInventory());
            for (int i = 0; i < player.inventory.getSizeInventory(); i++)
            {
                previous[i] = player.inventory.getStackInSlot(i);
            }
            return;
        }

        final List<Pair<ItemStack, ItemStack>> changes = Lists.newArrayList();
        for (int i = 0; i < player.inventory.getSizeInventory(); i++)
        {
            ItemStack stack = player.inventory.getStackInSlot(i);
            ItemStack old = previous[i];
            if (!areSameishItem(stack, old)
                    || (stack.getCount() != old.getCount()))
            {
                changes.add(Pair.of(old, stack));
            }
            previous[i] = stack.copy();
        }

        ItemStack stackInCursor = player.inventory.getItemStack();
        if (!areSameishItem(stackInCursor, previousInCursor)
                || (stackInCursor.getCount() != previousInCursor.getCount()))
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
                int difference = right.getCount() - left.getCount();
                if (difference > 0)
                    obtainedItem(changeList, left, difference);
                else if (difference < 0)
                    lostItem(changeList, left, -difference);
            }
            else
            {
                if (!leftEmpty)
                {
                    lostItem(changeList, left, left.getCount());
                }
                if (!rightEmpty)
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

    private static boolean areSameishItem(ItemStack a, ItemStack b)
    {
        return a.getCount() <= 0 && b.getCount() <= 0
                || (a.isItemEqual(b) && ItemStack.areItemStackTagsEqual(a, b));
    }

    private void obtainedItem(List<ChangeInfo> changeList, ItemStack item, int added)
    {
        if (added <= 0)
            return;

        accumulate(changeList, item, 1, added, true);
    }

    private void lostItem(List<ChangeInfo> changeList, ItemStack item, int removed)
    {
        if (removed <= 0)
            return;

        accumulate(changeList, item, 2, removed, true);
    }

    private void accumulate(List<ChangeInfo> changeList, ItemStack stack, int mode, int count, boolean isLocal)
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
            info.mode = info.mode == 1 ? 2 : 1;
        }
    }

    private static class ChangeInfo
    {
        public final ComparableItem item;
        public int mode;
        public int count;
        public int ttl;

        public ChangeInfo(ComparableItem item, int mode, int count, int ttl)
        {
            this.item = item;
            this.mode = mode;
            this.count = count;
            this.ttl = ttl;
        }
    }

    private static class ComparableItem
    {
        ItemStack stack;

        public ComparableItem(ItemStack stack)
        {
            this.stack = stack.copy();
            this.stack.setCount(1);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof ComparableItem))
                return false;
            ItemStack stack = ((ComparableItem) obj).stack;
            return areSameishItem(stack, this.stack);
        }

        @Override
        public int hashCode()
        {
            return stack.getItem().hashCode() * 31 ^ stack.getMetadata();
        }
    }
}
