package notryken.effecttimerplus;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import notryken.effecttimerplus.gui.screen.ConfigScreen;

@Mod(EffectTimerPlus.MOD_ID)
public class EffectTimerPlusNeoForge {
    public EffectTimerPlusNeoForge() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (client, parent) -> new ConfigScreen(parent))
                );

        EffectTimerPlus.init();
    }
}