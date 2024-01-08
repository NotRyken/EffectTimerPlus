package notryken.effecttimer;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import notryken.effecttimer.gui.screen.GlobalConfigScreen;

@Mod(Constants.MOD_ID)
public class EffectTimerForge {
    public EffectTimerForge() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (client, parent) -> new GlobalConfigScreen(parent))
                );

        EffectTimer.init();
    }
}