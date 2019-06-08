package gigaherz.inventoryspam;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class PlayerContainerHooks
{
    private static PlayerContainer original;
    private static Runnable callback;

    public static void afterSetAll(PlayerContainer playerContainer, List<ItemStack> stacks)
    {
        if (playerContainer == original && callback != null)
            callback.run();
    }

    public static void setTarget(Container container, Runnable callback)
    {
        PlayerContainerHooks.original = (net.minecraft.inventory.container.PlayerContainer)container;
        PlayerContainerHooks.callback = callback;
    }
}
