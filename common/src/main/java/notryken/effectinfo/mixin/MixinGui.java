package notryken.effectinfo.mixin;

import com.google.common.collect.Ordering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import notryken.effectinfo.EffectInfo;
import notryken.effectinfo.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(value = Gui.class, priority = 500)
public class MixinGui {

    @Final
    @Shadow
    private Minecraft minecraft;
    @Shadow
    private int screenWidth;

    @Inject(method = "renderEffects", at = @At("TAIL"))
    private void renderDurationOverlay(GuiGraphics graphics, CallbackInfo ci) {
        Collection<MobEffectInstance> effects = this.minecraft.player.getActiveEffects();
        if (!effects.isEmpty()) {

            // Replicate vanilla placement algorithm to place labels correctly
            int beneficialCount = 0;
            int nonBeneficialCount = 0;

            for (MobEffectInstance effectInstance : Ordering.natural().reverse().sortedCopy(effects)) {
                MobEffect effect = effectInstance.getEffect();
                if (effectInstance.showIcon()) {
                    int x = this.screenWidth;
                    int y = 1;
                    if (this.minecraft.isDemo()) {
                        y += 15;
                    }

                    if (effect.isBeneficial()) {
                        ++beneficialCount;
                        x -= 25 * beneficialCount;
                    } else {
                        ++nonBeneficialCount;
                        x -= 25 * nonBeneficialCount;
                        y += 26;
                    }

                    if (EffectInfo.config().potencyEnabled && effectInstance.getAmplifier() > 0) {
                        String amplifierStr = Util.getAmplifierAsString(effectInstance.getAmplifier());
                        int amplifierLen = minecraft.font.width(amplifierStr);
                        int pX;
                        int pY;
                        switch(EffectInfo.config().potencyLocation) {
                            case 0:
                                pX = x + 3;
                                pY = y + 3;
                                break;
                            case 1:
                                pX = x + 22 - amplifierLen;
                                pY = y + 3;
                                break;
                            case 2:
                                pX = x + 3;
                                pY = y + 14;
                                break;
                            case 3:
                                pX = x + 22 - amplifierLen;
                                pY = y + 14;
                                break;
                            default:
                                pX = x + 22 - amplifierLen;
                                pY = y + 3;
                                EffectInfo.LOG.error(
                                        "Unexpected value {} for potency location. Allowed values: 0-4",
                                        EffectInfo.config().potencyLocation);
                                break;
                        }
                        graphics.fill(pX, pY, pX + amplifierLen, pY + minecraft.font.lineHeight - 1,
                                EffectInfo.config().potencyBgColor);
                        graphics.drawString(minecraft.font, amplifierStr, pX, pY, EffectInfo.config().potencyColor, false);
                    }
                    if (EffectInfo.config().countdownEnabled && (EffectInfo.config().ambientCountdownEnabled || !effectInstance.isAmbient())) {
                        String durationStr = Util.getDurationAsString(effectInstance.getDuration());
                        int amplifierLen = minecraft.font.width(durationStr);
                        int pX;
                        int pY;
                        switch(EffectInfo.config().countdownLocation) {
                            case 0:
                                pX = x + 3;
                                pY = y + 3;
                                break;
                            case 1:
                                pX = x + 22 - amplifierLen;
                                pY = y + 3;
                                break;
                            case 2:
                                pX = x + 3;
                                pY = y + 14;
                                break;
                            case 3:
                                pX = x + 22 - amplifierLen;
                                pY = y + 14;
                                break;
                            default:
                                pX = x + 3;
                                pY = y + 14;
                                EffectInfo.LOG.error(
                                        "Unexpected value {} for potency location. Allowed values: 0-4",
                                        EffectInfo.config().potencyLocation);
                                break;
                        }
                        graphics.fill(pX, pY, pX + amplifierLen, pY + minecraft.font.lineHeight - 1,
                                EffectInfo.config().countdownBgColor);
                        int color = Util.getColor(effectInstance);
                        graphics.drawString(minecraft.font, durationStr, pX, pY, color, false);
                    }
                }
            }
        }
    }
}
