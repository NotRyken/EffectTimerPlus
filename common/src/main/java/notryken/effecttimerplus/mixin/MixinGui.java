package notryken.effecttimerplus.mixin;

import com.google.common.collect.Ordering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import notryken.effecttimerplus.EffectTimerPlus;
import notryken.effecttimerplus.util.Util;
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

        // Replicate vanilla placement algorithm to place labels correctly
        Collection<MobEffectInstance> effects = this.minecraft.player.getActiveEffects();
        if (!effects.isEmpty()) {

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

                    // Render potency overlay
                    if (EffectTimerPlus.config().potencyEnabled && effectInstance.getAmplifier() > 0) {
                        String label = Util.getAmplifierAsString(effectInstance.getAmplifier());
                        int labelWidth = minecraft.font.width(label);
                        int posX = x + Util.getTextOffsetX(EffectTimerPlus.config().getPotencyLocation(), labelWidth);
                        int posY = y + Util.getTextOffsetY(EffectTimerPlus.config().getPotencyLocation());
                        graphics.fill(posX, posY, posX + labelWidth, posY + minecraft.font.lineHeight - 1,
                                EffectTimerPlus.config().getPotencyBackColor());
                        graphics.drawString(minecraft.font, label, posX, posY, EffectTimerPlus.config().getPotencyColor(), false);
                    }
                    // Render timer overlay
                    if (EffectTimerPlus.config().timerEnabled && (EffectTimerPlus.config().timerEnabledAmbient || !effectInstance.isAmbient())) {
                        String label = Util.getDurationAsString(effectInstance.getDuration());
                        int labelWidth = minecraft.font.width(label);
                        int posX = x + Util.getTextOffsetX(EffectTimerPlus.config().getTimerLocation(), labelWidth);
                        int posY = y + Util.getTextOffsetY(EffectTimerPlus.config().getTimerLocation());
                        graphics.fill(posX, posY, posX + labelWidth, posY + minecraft.font.lineHeight - 1,
                                EffectTimerPlus.config().getTimerBackColor());
                        int color = Util.getTimerColor(effectInstance);
                        graphics.drawString(minecraft.font, label, posX, posY, color, false);
                    }
                }
            }
        }
    }
}
