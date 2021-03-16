package gigaherz.inventoryspam.mixin;

import gigaherz.inventoryspam.PlayerContainerHooks;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = PlayerContainer.class, priority = 0)
public abstract class PlayerContainerMixin extends RecipeBookContainer<CraftingInventory>
{
    public PlayerContainerMixin(ContainerType<?> type, int id)
    {
        super(type, id);
    }

    @Override
    public void setAll(List<ItemStack> stacks)
    {
        super.setAll(stacks);
    }

    @Inject(method = "setAll(Ljava/util/List;)V", at = @At("RETURN"))
    public void setAllHook(List<ItemStack> stacks, CallbackInfo callbackInfo)
    {
        PlayerContainerHooks.afterSetAll((PlayerContainer)(Object)this);
    }
}
