package notryken.effecttimerplus;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import notryken.effecttimerplus.gui.screen.ConfigScreen;

@Mod(EffectTimerPlus.MOD_ID)
public class EffectTimerPlusForge {
    public EffectTimerPlusForge() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (client, parent) -> new ConfigScreen(parent))
        );

        EffectTimerPlus.init();
    }
}