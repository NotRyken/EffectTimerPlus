package notryken.effectinfo;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import notryken.effectinfo.gui.screen.GlobalConfigScreen;

@Mod(Constants.MOD_ID)
public class EffectInfoNeoForge {
    public EffectInfoNeoForge() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (client, parent) -> new GlobalConfigScreen(parent))
                );

        EffectInfo.init();
    }
}