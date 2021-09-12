package gigaherz.inventoryspam;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;

public class PlayerContainerHooks
{
    private static InventoryMenu original;
    private static Runnable callback;

    public static void afterSetAll(InventoryMenu playerContainer)
    {
        if (playerContainer == original && callback != null)
            callback.run();
    }

    public static void setTarget(AbstractContainerMenu container, Runnable callback)
    {
        PlayerContainerHooks.original = (net.minecraft.world.inventory.InventoryMenu)container;
        PlayerContainerHooks.callback = callback;
    }

    public static InventoryMenu getOriginalContainer()
    {
        return original;
    }
}
