package walksy.test.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.test.TestMod;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"))
    public void onKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci)
    {
        if (action == GLFW.GLFW_PRESS)
        {
            if (key == GLFW.GLFW_KEY_INSERT)
            {
                if (TestMod.enabled) {
                    MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("Disabled Test Mod"));
                } else {
                    MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("Enabled Test Mod"));
                }
                TestMod.enabled = !TestMod.enabled;
            }
        }
    }
}
