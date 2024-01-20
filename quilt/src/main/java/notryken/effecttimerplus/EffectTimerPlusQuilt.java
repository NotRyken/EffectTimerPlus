package notryken.effecttimerplus;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class EffectTimerPlusQuilt implements ClientModInitializer {
    @Override
    public void onInitializeClient(ModContainer mod) {
        EffectTimerPlus.init();
    }
}