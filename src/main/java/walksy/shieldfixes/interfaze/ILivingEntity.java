package walksy.shieldfixes.interfaze;

import net.minecraft.world.item.ItemStack;

public interface ILivingEntity {
    void setActiveItem(ItemStack stack);
    void setItemUseTime(int time);
}
