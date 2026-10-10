package walksy.shieldfixes.manager;

import main.walksy.lib.core.WalksyLib;
import main.walksy.lib.core.mods.data.ShieldStateDataTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import walksy.shieldfixes.interfaze.ILivingEntity;

public final class ShieldStateManager {
    private static final int IDLE_USE_TIME = 72000;

    private ShieldStateManager() {
    }

    public static void syncRemoteShieldUse() {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        final ShieldStateDataTracker state = WalksyLib.getInstance().getShieldStateData();
        for (final Player player : minecraft.level.players()) {
            if (player == minecraft.player || !state.isUsingShield(player, false)) {
                continue;
            }
            ((ILivingEntity) player).setActiveItem(player.getItemInHand(player.getUsedItemHand()));
            ((ILivingEntity) player).setItemUseTime(IDLE_USE_TIME - state.shieldUseTicks(player));
        }
    }

    public static boolean isUsingShield(final Player player, final boolean delay) {
        return WalksyLib.getInstance().getShieldStateData().isUsingShield(player, delay);
    }

    public static boolean isUsingShield(final Player player, final Vec3 source, final boolean delay) {
        if (!isUsingShield(player, delay)) {
            return false;
        }
        if (source == null) {
            return true;
        }
        final Vec3 viewVector = player.getViewVector(1);
        final Vec3 vectorTo = source.vectorTo(player.position()).normalize();
        final Vec3 flat = new Vec3(vectorTo.x, 0.0, vectorTo.z);
        return flat.dot(viewVector) < 0.0;
    }

    public static boolean disablesShield(final Player player) {
        return WalksyLib.getInstance().getShieldStateData().disablesShield(player);
    }
}
