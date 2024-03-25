package walksy.shield.manager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class PlayerShieldingManager {

    /**
     * This concoction of code was made at 2am
     */

    public ArrayList<ShieldingPlayer> shieldingPlayers = new ArrayList<>();
    public boolean eventCanceled = false;

    /**
     * This code uses the #usingShield(LivingEntity) method to collect an arraylist of players shielding
     * However they're not actually shielding due to it not factoring in the 5 tick delay implemented by Minecraft
     * So we factor this in ourselves (See #ShieldingPlayer)
     */
    public void tick() {
        shieldingPlayers.forEach(ShieldingPlayer::tick);
        shieldingPlayers.removeIf(shieldingPlayer -> !usingShield(shieldingPlayer.getPlayer()));
        MinecraftClient.getInstance().world.getPlayers().forEach(player -> {
            if (usingShield(player)) {
                boolean playerAlreadyExists = false;
                for (ShieldingPlayer shieldingPlayer : shieldingPlayers) {
                    if (shieldingPlayer.getPlayer() == player) {
                        playerAlreadyExists = true;
                        break;
                    }
                }
                if (!playerAlreadyExists) {
                    shieldingPlayers.add(new ShieldingPlayer(player));
                }
            }
        });
    }


    boolean isHoldingShield(LivingEntity entity)
    {
        return entity.getMainHandStack().isOf(Items.SHIELD) || entity.getOffHandStack().isOf(Items.SHIELD);
    }

    /**
     * This boolean doesn't actually return the accurate state of a player shielding or not
     * However the LivingEntity.isBlocking() method is also inaccurate due to the server not recording the player's itemUseTime correctly
     * This also means other animations like throwing a trident or loading a crossbow will return inaccurate animations... buuuuut this mod is for shields only :3
     */
    boolean usingShield(LivingEntity entity)
    {
        return entity.isUsingItem() && isHoldingShield(entity) && !eventCanceled;
    }
}
