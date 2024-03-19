package notryken.effecttimerplus.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import notryken.effecttimerplus.util.MiscUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static notryken.effecttimerplus.EffectTimerPlus.config;

/**
 * Includes derivative work of code used by
 * <a href="https://github.com/magicus/statuseffecttimer/">Status Effect Timer</a>
 * <p>
 * Yes, this directly copies Minecraft code. Is that ideal? No, but I've spent
 * so much time failing to get it working any other way that I'm beyond caring.
 */
@Mixin(value = Gui.class, priority = 500)
public class MixinGui {

    @Final
    @Shadow
    private Minecraft minecraft;

    @SuppressWarnings("unchecked")
    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    private void renderEffectsAndOverlay(GuiGraphics graphics, CallbackInfo ci) {
        Collection effects;
        label40: {
            effects = this.minecraft.player.getActiveEffects();
            if (!effects.isEmpty()) {
                Screen screen = this.minecraft.screen;
                if (!(screen instanceof EffectRenderingInventoryScreen)) {
                    break label40;
                }

                EffectRenderingInventoryScreen invScreen = (EffectRenderingInventoryScreen)screen;
                if (!invScreen.canSeeEffects()) {
                    break label40;
                }
            }
            return;
        }

        float scale = (float) config().scale;
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 0.0F);

        RenderSystem.enableBlend();
        int column = 0;
        int row = 0;
        MobEffectTextureManager textureManager = this.minecraft.getMobEffectTextures();
        List<Runnable> runnables = Lists.newArrayListWithExpectedSize(effects.size());
        Iterator iter = Ordering.natural().reverse().sortedCopy(effects).iterator();

        while(iter.hasNext()) {
            MobEffectInstance effectInstance = (MobEffectInstance)iter.next();
            MobEffect effect = effectInstance.getEffect();
            if (effectInstance.showIcon()) {
                int right = (int)(graphics.guiWidth() / scale);
                int top = 1;
                if (this.minecraft.isDemo()) {
                    top += 15;
                }

                if (effect.isBeneficial()) {
                    ++column;
                    right -= 25 * column;
                } else {
                    ++row;
                    right -= 25 * row;
                    top += 26;
                }

                float alpha;
                if (effectInstance.isAmbient()) {
                    alpha = 1.0F;
                    graphics.blitSprite(Gui.EFFECT_BACKGROUND_AMBIENT_SPRITE, right, top, 24, 24);
                } else {
                    graphics.blitSprite(Gui.EFFECT_BACKGROUND_SPRITE, right, top, 24, 24);
                    if (effectInstance.endsWithin(200)) {
                        int duration = effectInstance.getDuration();
                        int $$13 = 10 - duration / 20;
                        alpha = Mth.clamp((float)duration / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + Mth.cos((float)duration * 3.1415927F / 5.0F) * Mth.clamp((float)$$13 / 10.0F * 0.25F, 0.0F, 0.25F);
                    } else {
                        alpha = 1.0F;
                    }
                }

                TextureAtlasSprite sprite = textureManager.get(effect);
                int finalRight = right;
                int finalTop = top;
                runnables.add(() -> {
                    graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
                    graphics.blit(finalRight + 3, finalTop + 3, 0, 18, 18, sprite);
                    graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                });
                runnables.add(() -> {
                    if (config().potencyEnabled && effectInstance.getAmplifier() > 0) {
                        String label = MiscUtil.getAmplifierAsString(effectInstance.getAmplifier());
                        int labelWidth = minecraft.font.width(label);
                        int posX = finalRight + MiscUtil.getTextOffsetX(config().getPotencyLocation(), labelWidth);
                        int posY = finalTop + MiscUtil.getTextOffsetY(config().getPotencyLocation());
                        graphics.fill(posX, posY, posX + labelWidth, posY + minecraft.font.lineHeight - 1,
                                config().getPotencyBackColor());
                        graphics.drawString(minecraft.font, label, posX, posY, config().getPotencyColor(), false);
                    }
                    // Render timer overlay
                    if (config().timerEnabled && (config().timerEnabledAmbient || !effectInstance.isAmbient())) {
                        String label = MiscUtil.getDurationAsString(effectInstance.getDuration());
                        int labelWidth = minecraft.font.width(label);
                        int posX = finalRight + MiscUtil.getTextOffsetX(config().getTimerLocation(), labelWidth);
                        int posY = finalTop + MiscUtil.getTextOffsetY(config().getTimerLocation());
                        graphics.fill(posX, posY, posX + labelWidth, posY + minecraft.font.lineHeight - 1,
                                config().getTimerBackColor());
                        int color = MiscUtil.getTimerColor(effectInstance);
                        graphics.drawString(minecraft.font, label, posX, posY, color, false);
                    }
                });
            }
        }

        runnables.forEach(Runnable::run);
        graphics.pose().popPose();
        ci.cancel();
    }
}
