package notryken.effectinfo;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class EffectInfoQuilt implements ClientModInitializer {
    @Override
    public void onInitializeClient(ModContainer mod) {
        EffectInfo.init();
    }
}