package notryken.effecttimerplus.mixin;

import com.google.common.collect.Ordering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import notryken.effecttimerplus.util.MiscUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

import static notryken.effecttimerplus.EffectTimerPlus.config;

/**
 * Includes derivative work of code used by
 * <a href="https://github.com/magicus/statuseffecttimer/">Status Effect Timer</a>
 */
@Mixin(value = Gui.class, priority = 500)
public class MixinGui {

    @Final
    @Shadow
    private Minecraft minecraft;

    @Shadow
    private int screenWidth;

    @Unique
    private boolean effectTimerPlus$operate;

    @Inject(method = "renderEffects", at = @At("HEAD"))
    private void scaleGraphics(GuiGraphics graphics, CallbackInfo ci) {
        effectTimerPlus$operate = false;
        Collection effects;
        label40:
        {
            effects = this.minecraft.player.getActiveEffects();
            if (!effects.isEmpty()) {
                Screen screen = this.minecraft.screen;
                if (!(screen instanceof EffectRenderingInventoryScreen)) {
                    break label40;
                }

                EffectRenderingInventoryScreen invScreen = (EffectRenderingInventoryScreen) screen;
                if (!invScreen.canSeeEffects()) {
                    break label40;
                }
            }
            return;
        }
        float scale = (float) config().scale;
        effectTimerPlus$operate = true;
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 0.0F);
        this.screenWidth = (int) (graphics.guiWidth() / scale);
    }

    @Inject(method = "renderEffects", at = @At("RETURN"))
    private void descaleGraphicsAndOverlay(GuiGraphics graphics, CallbackInfo ci) {
        if (effectTimerPlus$operate) {
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
                        if (config().potencyEnabled && effectInstance.getAmplifier() > 0) {
                            String label = MiscUtil.getAmplifierAsString(effectInstance.getAmplifier());
                            int labelWidth = minecraft.font.width(label);
                            int posX = x + MiscUtil.getTextOffsetX(config().getPotencyLocation(), labelWidth);
                            int posY = y + MiscUtil.getTextOffsetY(config().getPotencyLocation());
                            graphics.fill(posX, posY, posX + labelWidth, posY + minecraft.font.lineHeight - 1,
                                    config().getPotencyBackColor());
                            graphics.drawString(minecraft.font, label, posX, posY, config().getPotencyColor(), false);
                        }
                        // Render timer overlay
                        if (config().timerEnabled && (config().timerEnabledAmbient || !effectInstance.isAmbient())) {
                            String label = MiscUtil.getDurationAsString(effectInstance.getDuration());
                            int labelWidth = minecraft.font.width(label);
                            int posX = x + MiscUtil.getTextOffsetX(config().getTimerLocation(), labelWidth);
                            int posY = y + MiscUtil.getTextOffsetY(config().getTimerLocation());
                            graphics.fill(posX, posY, posX + labelWidth, posY + minecraft.font.lineHeight - 1,
                                    config().getTimerBackColor());
                            int color = MiscUtil.getTimerColor(effectInstance);
                            graphics.drawString(minecraft.font, label, posX, posY, color, false);
                        }
                    }
                }
                graphics.pose().popPose();
                this.screenWidth = graphics.guiWidth();
            }
        }
    }
}
