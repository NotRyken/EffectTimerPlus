package notryken.effecttimer;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class EffectTimerQuilt implements ClientModInitializer {
    @Override
    public void onInitializeClient(ModContainer mod) {
        EffectTimer.init();
    }
}