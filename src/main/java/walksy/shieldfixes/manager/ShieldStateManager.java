package walksy.shieldfixes.manager;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import walksy.shieldfixes.interfaze.ILivingEntity;

import java.util.HashMap;
import java.util.Map;

public class ShieldStateManager {

    private final Minecraft minecraft = Minecraft.getInstance();
    private final Map<Player, Integer> playerMap = new HashMap<>();

    public void update() {
        for (Player player : this.minecraft.level.players()) {
            if (player != this.minecraft.player) {
                int useTime = this.playerMap.getOrDefault(player, 72000); //create an integer map with each instance of a player

                if (player.isUsingItem() && this.isHoldingUsableShield(player)) {
                    ((ILivingEntity) player).setActiveItem(player.getItemInHand(player.getUsedItemHand()));
                    ((ILivingEntity) player).setItemUseTime(useTime);
                    useTime--;
                } else {
                    useTime = 72000;
                }

                this.playerMap.put(player, useTime);
            }
        }
    }

    public static boolean isUsingShield(Player player, boolean delay) {
        return isUsingShield(player, null, delay);
    }

    public static boolean isUsingShield(Player player, Vec3 source, boolean delay) {
        boolean usingShield = delay
            ? player.isBlocking()
            //? if >=1.21.11 {
            : (player.isUsingItem() && player.getActiveItem().is(Items.SHIELD));
            //?} else
            //: (player.isUsingItem() && player.getUseItem().is(Items.SHIELD));

        if (!usingShield) return false;
        if (source == null) return true;

        //LivingEntity#applyItemBlocking
        Vec3 viewVector = player.getViewVector(1);
        Vec3 vectorTo = source.vectorTo(player.position()).normalize();
        Vec3 flat = new Vec3(vectorTo.x, 0.0, vectorTo.z);
        return flat.dot(viewVector) < 0.0;
    }



    private boolean isHoldingUsableShield(Player entity) {
        return (entity.getMainHandItem().is(Items.SHIELD) || entity.getOffhandItem().is(Items.SHIELD)) && !isHoldingAnimationItemMainHand(entity);
    }

    private boolean isHoldingAnimationItemMainHand(Player entity) {
        return entity.getMainHandItem().getUseDuration(entity) != 0
            && !entity.getMainHandItem().is(Items.SHIELD);
    }

    public static boolean disablesShield(Player player) {
        return player.getWeaponItem().getItem() instanceof AxeItem;
    }
}
