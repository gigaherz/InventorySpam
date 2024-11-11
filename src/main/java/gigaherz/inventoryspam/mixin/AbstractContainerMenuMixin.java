package gigaherz.inventoryspam.mixin;

import gigaherz.inventoryspam.PlayerContainerHooks;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin
{
    @Inject(method = "initializeContents(ILjava/util/List;Lnet/minecraft/world/item/ItemStack;)V", at = @At("RETURN"))
    public void initializeContentsHook(int id, List<ItemStack> items, ItemStack carried, CallbackInfo callbackInfo)
    {
        if ((Object)this instanceof InventoryMenu inv)
        {
            //noinspection ConstantConditions
            PlayerContainerHooks.afterSetAll(inv);
        }
    }
}
