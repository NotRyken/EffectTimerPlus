package notryken.effectinfo;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import notryken.effectinfo.gui.screen.GlobalConfigScreen;

@Mod(Constants.MOD_ID)
public class EffectInfoForge {
    public EffectInfoForge() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (client, parent) -> new GlobalConfigScreen(parent))
                );

        EffectInfo.init();
    }
}