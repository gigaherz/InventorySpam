package gigaherz.inventoryspam.mixin;

import gigaherz.inventoryspam.PlayerContainerHooks;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = InventoryMenu.class, priority = 0)
public abstract class PlayerContainerMixin extends RecipeBookMenu<CraftingContainer>
{
    public PlayerContainerMixin(MenuType<?> type, int id)
    {
        super(type, id);
    }

    @Override
    public void initializeContents(int i, List<ItemStack> stacks, ItemStack held)
    {
        super.initializeContents(i, stacks, held);
    }

    @SuppressWarnings("MixinAnnotationTarget")
    @Inject(method = "initializeContents(ILjava/util/List;Lnet/minecraft/world/item/ItemStack;)V", at = @At("RETURN"))
    public void initializeContentsHook(int i, List<ItemStack> stacks, ItemStack held, CallbackInfo callbackInfo)
    {
        //noinspection ConstantConditions
        PlayerContainerHooks.afterSetAll((InventoryMenu)(Object)this);
    }
}
