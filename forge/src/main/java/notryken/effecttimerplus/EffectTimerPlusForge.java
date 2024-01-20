package notryken.effecttimerplus;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import notryken.effecttimerplus.gui.screen.GlobalConfigScreen;

@Mod(Constants.MOD_ID)
public class EffectTimerPlusForge {
    public EffectTimerPlusForge() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (client, parent) -> new GlobalConfigScreen(parent))
                );

        EffectTimerPlus.init();
    }
}