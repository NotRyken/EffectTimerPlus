package notryken.effecttimerplus;

import net.fabricmc.api.ClientModInitializer;

public class EffectTimerPlusFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EffectTimerPlus.init();
    }
}