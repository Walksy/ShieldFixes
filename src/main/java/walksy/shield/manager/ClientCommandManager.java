package walksy.shield.manager;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

public class ClientCommandManager {

    public static ClientCommandManager INSTANCE = new ClientCommandManager();

    public void initCommand()
    {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("walksy5tickdelay")
                .executes(context -> {
                    ConfigManager.INSTANCE.factor5TickDelay = !ConfigManager.INSTANCE.factor5TickDelay;
                    String message = ConfigManager.INSTANCE.factor5TickDelay ? "enabled" : "disabled";
                    context.getSource().sendFeedback(Text.of("5 Tick Delay fix is now " + message));
                    ConfigManager.INSTANCE.saveConfig();
                    return 1;
                })
            )
        );
    }
}
