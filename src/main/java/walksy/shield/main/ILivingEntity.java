package walksy.shield.main;

import net.minecraft.item.ItemStack;

public interface ILivingEntity {
    void setActiveItem(ItemStack stack);
    void setItemUseTime(int time);
}
