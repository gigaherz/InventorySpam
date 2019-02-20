package gigaherz.inventoryspam;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;

public class ContainerWrapper extends ContainerPlayer
{
    private final ContainerPlayer original;
    private final Runnable callback;

    private static final Field f_listeners = findField(Container.class, ObfuscationReflectionHelper.remapName("field_75149_d"));

    private static Field findField(Class<?> clazz, String name)
    {
        try
        {
            Field f = clazz.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public ContainerWrapper(ContainerPlayer original, EntityPlayer player, Runnable callback)
    {
        super(player.inventory, original.isLocalWorld, player);
        this.original = original;
        this.callback = callback;
        this.craftMatrix = original.craftMatrix;
        this.craftResult = original.craftResult;
        this.isLocalWorld = original.isLocalWorld;

        this.inventoryItemStacks = original.inventoryItemStacks;
        this.inventorySlots = original.inventorySlots;
        this.windowId = original.windowId;
        try
        {
            this.listeners = (List<IContainerListener>) f_listeners.get(original);
        }
        catch (IllegalAccessException e)
        {
            // ignore.
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        if (original != null)
            original.onCraftMatrixChanged(inventoryIn);
    }

    @Override
    protected Slot addSlot(Slot slotIn)
    {
        // Ignore -- the original container keeps the list of slots
        return slotIn;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        original.onContainerClosed(playerIn);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return original.canInteractWith(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        return original.transferStackInSlot(playerIn, index);
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return original.canMergeSlot(stack, slotIn);
    }

    @Override
    public void addListener(IContainerListener listener)
    {
        original.addListener(listener);
    }

    @Override
    public NonNullList<ItemStack> getInventory()
    {
        return original.getInventory();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void removeListener(IContainerListener listener)
    {
        original.removeListener(listener);
    }

    @Override
    public void detectAndSendChanges()
    {
        original.detectAndSendChanges();
    }

    @Override
    public boolean enchantItem(EntityPlayer playerIn, int id)
    {
        return original.enchantItem(playerIn, id);
    }

    @Override
    @Nullable
    public Slot getSlotFromInventory(IInventory inv, int slotIn)
    {
        return original.getSlotFromInventory(inv, slotIn);
    }

    @Override
    public Slot getSlot(int slotId)
    {
        return original.getSlot(slotId);
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
    {
        return original.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public void putStackInSlot(int slotID, ItemStack stack)
    {
        original.putStackInSlot(slotID, stack);
    }

    @Override
    public void setAll(List<ItemStack> p_190896_1_)
    {
        original.setAll(p_190896_1_);
        callback.run();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void updateProgressBar(int id, int data)
    {
        original.updateProgressBar(id, data);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public short getNextTransactionID(InventoryPlayer invPlayer)
    {
        return original.getNextTransactionID(invPlayer);
    }

    @Override
    public boolean getCanCraft(EntityPlayer player)
    {
        return original.getCanCraft(player);
    }

    @Override
    public void setCanCraft(EntityPlayer player, boolean canCraft)
    {
        original.setCanCraft(player, canCraft);
    }

    @Override
    public boolean canDragIntoSlot(Slot slotIn)
    {
        return original.canDragIntoSlot(slotIn);
    }

    @Override
    public void func_201771_a(RecipeItemHelper p_201771_1_)
    {
        original.func_201771_a(p_201771_1_);
    }

    @Override
    public void clear()
    {
        original.clear();
    }

    @Override
    public boolean matches(IRecipe p_201769_1_)
    {
        return original.matches(p_201769_1_);
    }

    @Override
    public int getOutputSlot()
    {
        return original.getOutputSlot();
    }

    @Override
    public int getWidth()
    {
        return original.getWidth();
    }

    @Override
    public int getHeight()
    {
        return original.getHeight();
    }

    @Override
    public int getSize()
    {
        return original.getSize();
    }
}
