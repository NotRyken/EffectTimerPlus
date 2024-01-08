package notryken.effecttimer.mixin;

import com.google.common.collect.Ordering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
            // Replicate vanilla placement algorithm to get the duration
            // labels to line up exactly right.

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

                    String duration = effectTimer$getDurationAsString(effectInstance);
                    int durationLength = minecraft.font.width(duration);
                    graphics.drawString(minecraft.font, duration, x + 13 - (durationLength / 2), y + 14, 0x99FFFFFF, false);

                    if (effectInstance.getAmplifier() > 0) {
                        String amplifierString = effectTimer$getAmplifierAsString(effectInstance);
                        int amplifierLength = minecraft.font.width(amplifierString);
                        graphics.drawString(minecraft.font, amplifierString, x + 22 - amplifierLength, y + 3, 0x99FFFFFF, false);
                    }
                }
            }
        }
    }

    @Unique
    private String effectTimer$getAmplifierAsString(MobEffectInstance effectInstance) {
        int value = effectInstance.getAmplifier() + 1;
        if (value > 1) {
            String key = String.format("enchantment.level.%d", value);
            if (I18n.exists(key)) {
                return I18n.get(key);
            } else {
                return String.valueOf(value);
            }
        }
        return "";
    }

    @Unique
    private String effectTimer$getDurationAsString(MobEffectInstance effectInstance) {
        int duration = effectInstance.getDuration();
        if(duration == MobEffectInstance.INFINITE_DURATION) {
            return "\u221e";
        }
        int seconds = Mth.floor((float) duration) / 20;
        if (seconds > 3600) {
            return ">1h";
        }
        else if (seconds >= 600) {
            return seconds / 60 + "m";
        }
        else if (seconds > 60) {
            int remainder = seconds % 60;
            return seconds / 60 + ":" + (remainder > 9 ? remainder : "0" + remainder);
        }
        else {
            return String.valueOf(seconds);
        }
    }
}
