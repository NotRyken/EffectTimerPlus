package notryken.effectinfo.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import notryken.effectinfo.EffectInfo;
import notryken.effectinfo.gui.component.CountdownListWidget;
import notryken.effectinfo.gui.component.PotencyListWidget;
import notryken.effectinfo.mixin.MixinGui;
import notryken.effectinfo.util.Util;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.List;

public class ConfigScreen extends OptionsSubScreen {

    private final int minX = this.width / 2 - 155;
    private final int minY = this.height / 6 - 12;
    private final int unitWidth = 150;
    private final int unitHeight = 20;
    private final int unitSpacing = 10;

    // effect, duration, amplifier, ambient, visible
    private final MobEffectInstance[] effects = new MobEffectInstance[] {
            new MobEffectInstance(MobEffects.DIG_SPEED, 111, 1, true, true),
            new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 211, 1, true, true),
            new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 411, 2, false, true),
            new MobEffectInstance(MobEffects.DAMAGE_BOOST, 811, 9, false, true),
            new MobEffectInstance(MobEffects.JUMP, 1251, 4, true, true),
            new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2131, 0, false, true),
            new MobEffectInstance(MobEffects.WEAKNESS, 3500, 1, false, true),
            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 9600, 0, false, true),
            new MobEffectInstance(MobEffects.INVISIBILITY, 144000, 0, false, true),
            new MobEffectInstance(MobEffects.CONDUIT_POWER, -1, 0, false, true),
    };

    // GUI elements
    private PotencyListWidget potencyList;
    private CountdownListWidget countdownList;
    private Button resetButton;
    private Button doneButton;


    public ConfigScreen(Screen lastScreen) {
        super(lastScreen, Minecraft.getInstance().options,
                Component.translatable("screen.effectinfo.title.default"));
    }

    @Override
    protected void init() {

        int paneTopY = minY + 65;
        int paneHeight = height - paneTopY - 36;
        int paneWidth = width / 2 - 8;
        int rightPaneX = width - paneWidth;

        potencyList = new PotencyListWidget(minecraft, paneWidth, paneHeight, paneTopY, 20, this);
        potencyList.setX(0);

        countdownList = new CountdownListWidget(minecraft, paneWidth, paneHeight, paneTopY, 20, this);
        countdownList.setX(rightPaneX);

        resetButton = Button.builder(Component.literal("Reset All"), (button) -> {
            EffectInfo.restoreDefaultConfig();
            reload();
        })
                .pos(this.width / 2 - 155, this.height - 29)
                .size(150, 20)
                .build();

        doneButton = Button.builder(CommonComponents.GUI_DONE, (button) -> onClose())
                .pos(this.width / 2 - 155 + 160, this.height - 29)
                .size(150, 20)
                .build();

        addRenderableWidget(potencyList);
        addRenderableWidget(countdownList);
        addRenderableWidget(resetButton);
        addRenderableWidget(doneButton);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(font, title, width / 2, 15, 16777215);

        int xSpace = 27;
        int x = width / 2 - effects.length * xSpace / 2;
        int y = minY + 40; // Icon placement corner is bottom left

        for (MobEffectInstance effect : effects) {
            graphics.blitSprite(Gui.EFFECT_BACKGROUND_SPRITE, x, y, 24, 24);
            graphics.blit(x + 3, y + 3, 0, 18, 18, minecraft.getMobEffectTextures().get(effect.getEffect()));

            int pLoc = EffectInfo.config().potencyLocation;
            int cLoc = EffectInfo.config().countdownLocation;
            String pStr = Util.getAmplifierAsString(effect.getAmplifier());
            String cStr = Util.getDurationAsString(effect.getDuration());
            int seconds = effect.getDuration() / 20;

            if (EffectInfo.config().potencyEnabled && effect.getAmplifier() > 0) {
                graphics.drawString(minecraft.font, pStr, pLoc % 2 == 0 ? x + 3 : x + 22 - minecraft.font.width(pStr),
                        pLoc < 2 ? y + 3 : y + 14, EffectInfo.config().potencyColor, false);
            }
            if (EffectInfo.config().countdownEnabled && (EffectInfo.config().ambientCountdownEnabled || !effect.isAmbient())) {
                int color = Util.getColor(effect);
                graphics.drawString(minecraft.font, cStr, cLoc % 2 == 0 ? x + 3 : x + 22 - minecraft.font.width(cStr),
                        cLoc < 2 ? y + 3 : y + 14, color, false);
            }
            x += xSpace;
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderDirtBackground(graphics);
    }

    @Override
    public void onClose() {
        EffectInfo.config().writeChanges();
        super.onClose();
    }

    public void reload() {
        Minecraft.getInstance().setScreen(new ConfigScreen(lastScreen));
    }
}
