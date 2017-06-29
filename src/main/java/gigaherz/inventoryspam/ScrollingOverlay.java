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
import net.minecraft.util.text.TextFormatting;
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

    private static final int TTL = 240;
    private static final int FADE = 40;

    private RenderItem renderItem;
    private int id;
    private ItemStack[] previous;
    private ItemStack previousInCursor = ItemStack.EMPTY;

    private final List<ChangeInfo> changeEntries = Lists.newArrayList();

    private ScrollingOverlay()
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

            GlStateManager.pushMatrix();
            GlStateManager.scale(Config.drawScale, Config.drawScale, 1);

            width = (int) (width / Config.drawScale);
            height = (int) (height / Config.drawScale);

            List<String[]> computedStrings = Lists.newArrayList();

            int rectWidth = 0;
            for (ChangeInfo change : changeEntries)
            {
                String name = change.item.stack.getDisplayName();
                String italics = change.item.stack.hasDisplayName() ? "" + TextFormatting.ITALIC : "";
                String mode = change.mode == 1 ? "+" : "-";
                String s1 = String.format("%s%d ", mode, change.count);
                String s2 = String.format("%s%s", italics, name);

                int w = font.getStringWidth(s1) + font.getStringWidth(s2);
                rectWidth = Math.max(rectWidth, w);
                computedStrings.add(new String[]{s1, s2});
            }

            int iconSize = (int) (16 * Config.iconScale);
            int rightMargin = Config.drawIcon ? (2 + iconSize) : 0;
            int topMargin1 = 2 + (Config.drawIcon ? Math.max(0, (iconSize - font.FONT_HEIGHT) / 2) : 0);
            int topMargin2 = 1 + Math.max(0, -(iconSize - font.FONT_HEIGHT) / 2);

            rectWidth += rightMargin;

            int lineHeight = font.FONT_HEIGHT;
            if (Config.drawIcon)
                lineHeight = Math.max(2 + iconSize, lineHeight);

            int rectHeight = lineHeight * number;

            int x, y;
            int align;
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
                String[] s = computedStrings.get(i);
                String s1 = s[0];
                String s2 = s[1];
                int w1 = font.getStringWidth(s1);
                int w = w1 + font.getStringWidth(s2);

                ChangeInfo change = changeEntries.get(i);
                int alpha = Math.min(255, change.ttl * 255 / FADE);
                int color = alpha << 24 | (change.mode == 1 ? 0x7FFF7F : 0xFF5F5F);

                int leftMargin = 0;
                switch (align)
                {
                    case -1:
                        leftMargin = 2;
                        break;
                    case 0:
                        leftMargin = (rectWidth - w - rightMargin) / 2;
                        break;
                    case 1:
                        leftMargin = rectWidth - w - rightMargin;
                        break;
                }

                GlStateManager.enableBlend();
                font.drawStringWithShadow(s1, x + leftMargin, y + topMargin1, color);
                font.drawStringWithShadow(s2, x + leftMargin + w1, y + topMargin1, color);

                if (Config.drawIcon)
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(x + 2 + w + leftMargin, y + topMargin2, 0);
                    GlStateManager.scale(Config.iconScale, Config.iconScale, 1);
                    RenderHelper.enableGUIStandardItemLighting();
                    renderItem.renderItemAndEffectIntoGUI(change.item.stack, 0, 0);
                    renderItem.renderItemOverlayIntoGUI(font, change.item.stack, 0, 0, null);
                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.popMatrix();
                }

                y += lineHeight;
            }

            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END)
            return;

        if (!Config.showItemAdditions && !Config.showItemRemovals)
            return;

        EntityPlayer player = Minecraft.getMinecraft().player;

        if (player == null)
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
            if (!areLooselyTheSame(stack, old)
                    || (stack.getCount() != old.getCount()))
            {
                changes.add(Pair.of(old, stack));
            }
            previous[i] = stack.copy();
        }

        ItemStack stackInCursor = player.inventory.getItemStack();
        if (!areLooselyTheSame(stackInCursor, previousInCursor)
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

    private static boolean areLooselyTheSame(ItemStack a, ItemStack b)
    {
        return a == b
                || isStackEmpty(a) && isStackEmpty(b)
                || ItemStack.areItemsEqualIgnoreDurability(a, b);
    }

    private static boolean areSameishItem(ItemStack a, ItemStack b)
    {
        return a == b
                || isStackEmpty(a) && isStackEmpty(b)
                || (ItemStack.areItemsEqual(a, b) && ItemStack.areItemStackTagsEqual(a, b));
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
        final ComparableItem item;
        int mode;
        int count;
        int ttl;

        ChangeInfo(ComparableItem item, int mode, int count, int ttl)
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

        ComparableItem(ItemStack stack)
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
