package notryken.effectinfo.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.effect.MobEffects;
import notryken.effectinfo.EffectInfo;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

/**
 * Main config screen.
 */
public class MainConfigScreen extends OptionsSubScreen {

    private final TextureAtlasSprite[] effectIcons = new TextureAtlasSprite[] {
        Minecraft.getInstance().getMobEffectTextures().get(MobEffects.DIG_SPEED),
                Minecraft.getInstance().getMobEffectTextures().get(MobEffects.MOVEMENT_SPEED),
                Minecraft.getInstance().getMobEffectTextures().get(MobEffects.DAMAGE_BOOST),
                Minecraft.getInstance().getMobEffectTextures().get(MobEffects.FIRE_RESISTANCE),
                Minecraft.getInstance().getMobEffectTextures().get(MobEffects.JUMP),
                Minecraft.getInstance().getMobEffectTextures().get(MobEffects.MOVEMENT_SLOWDOWN),
                Minecraft.getInstance().getMobEffectTextures().get(MobEffects.DAMAGE_RESISTANCE),
                Minecraft.getInstance().getMobEffectTextures().get(MobEffects.INVISIBILITY),
                Minecraft.getInstance().getMobEffectTextures().get(MobEffects.WEAKNESS),
                Minecraft.getInstance().getMobEffectTextures().get(MobEffects.CONDUIT_POWER),
    };

    private final int[] quickColors = new int[] {
            10027008,
            16711680,
            16753920,
            16761856,
            16776960,
            65280,
            32768,
            19456,
            2142890,
            65535,
            255,
            8388736,
            16711935,
            16777215,
            8421504,
            0};

    private final ToIntFunction<Integer> getAlpha = (value) -> (value >> 24 & 255);
    private final ToIntFunction<Integer> getRed = (value) -> (value >> 16 & 255);
    private final ToIntFunction<Integer> getGreen = (value) -> (value >> 8 & 255);
    private final ToIntFunction<Integer> getBlue = (value) -> (value & 255);
    private final ToIntFunction<Integer> setAlpha = (value) -> (value * 16777216);
    private final ToIntFunction<Integer> setRed = (value) -> (value * 65536);
    private final ToIntFunction<Integer> setGreen = (value) -> (value * 256);
    private final ToIntFunction<Integer> setBlue = (value) -> (value);

    public MainConfigScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options,
                Component.translatable("screen.effectinfo.title.default"));
    }

    @Override
    protected void init() {

        // Potency text color adjustment block
        int blockX = width / 2 - 223;
        int blockY = height / 2 - 50;
        int unitWidth = 220;
        int unitHeight = 20;
        Supplier<Integer> pSource = EffectInfo.config()::getPotencyColor;
        Supplier<Integer> cSource = EffectInfo.config()::getCountdownColor;

        addRenderableWidget(new StringWidget(blockX, blockY, unitWidth, unitHeight,
                Component.literal("Potency Text Color"), minecraft.font));

        // Quick color buttons
        int buttonWidth = unitWidth / quickColors.length;

        for (int i = 0; i < quickColors.length; i++) {
            int color = quickColors[i];
            int setX = blockX + (unitWidth / 2) - (buttonWidth * quickColors.length / 2);
            addRenderableWidget(Button.builder(Component.literal("\u2588")
                            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color))), (button) ->
                    {
                        int initial = pSource.get();
                        EffectInfo.config().setPotencyColor(
                                setAlpha.applyAsInt(getAlpha.applyAsInt(initial)) + color);
                        minecraft.setScreen(new MainConfigScreen(lastScreen));
                    })
                    .size(buttonWidth, buttonWidth)
                    .pos(setX + (buttonWidth * i), blockY + 20)
                    .build());
        }

        // Sliders
        addRenderableWidget(new ColorSlider(blockX, blockY + 40, unitWidth, unitHeight,
                "Alpha: ", pSource,
                (color) -> {
                    int initial = pSource.get();
                    EffectInfo.config().setPotencyColor(
                            setAlpha.applyAsInt(color)
                                    + setRed.applyAsInt(getRed.applyAsInt(initial))
                                    + setGreen.applyAsInt(getGreen.applyAsInt(initial))
                                    + setBlue.applyAsInt(getBlue.applyAsInt(initial)));
                },
                getAlpha, setAlpha));
        addRenderableWidget(new ColorSlider(blockX, blockY + 65, unitWidth, unitHeight,
                "Red: ", pSource,
                (color) -> {
                    int initial = pSource.get();
                    EffectInfo.config().setPotencyColor(
                            setAlpha.applyAsInt(getAlpha.applyAsInt(initial))
                                    + setRed.applyAsInt(color)
                                    + setGreen.applyAsInt(getGreen.applyAsInt(initial))
                                    + setBlue.applyAsInt(getBlue.applyAsInt(initial)));
                },
                getRed, setRed));
        addRenderableWidget(new ColorSlider(blockX, blockY + 90, unitWidth, unitHeight,
                "Green: ", pSource,
                (color) -> {
                    int initial = pSource.get();
                    EffectInfo.config().setPotencyColor(
                            setAlpha.applyAsInt(getAlpha.applyAsInt(initial))
                                    + setRed.applyAsInt(getRed.applyAsInt(initial))
                                    + setGreen.applyAsInt(color)
                                    + setBlue.applyAsInt(getBlue.applyAsInt(initial)));
                },
                getGreen, setGreen));
        addRenderableWidget(new ColorSlider(blockX, blockY + 115, unitWidth, unitHeight,
                "Blue: ", pSource,
                (color) -> {
                    int initial = pSource.get();
                    EffectInfo.config().setPotencyColor(
                            setAlpha.applyAsInt(getAlpha.applyAsInt(initial))
                                    + setRed.applyAsInt(getRed.applyAsInt(initial))
                                    + setGreen.applyAsInt(getGreen.applyAsInt(initial))
                                    + setBlue.applyAsInt(color));
                    },
                getBlue, setBlue));

        // Countdown text color adjustment block
        blockX = width / 2 + 3;

        addRenderableWidget(new StringWidget(blockX, blockY, unitWidth, unitHeight,
                Component.literal("Countdown Text Color"), minecraft.font));

        // Quick color buttons
        for (int i = 0; i < quickColors.length; i++) {
            int color = quickColors[i];
            int setX = blockX + (unitWidth / 2) - (buttonWidth * quickColors.length / 2);
            addRenderableWidget(Button.builder(Component.literal("\u2588")
                            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color))), (button) ->
                    {
                        int initial = cSource.get();
                        EffectInfo.config().setCountdownColor(
                                setAlpha.applyAsInt(getAlpha.applyAsInt(initial)) + color);
                        minecraft.setScreen(new MainConfigScreen(lastScreen));
                    })
                    .size(buttonWidth, 15)
                    .pos(setX + (buttonWidth * i), blockY + 20)
                    .build());
        }

        // Sliders
        addRenderableWidget(new ColorSlider(blockX, blockY + 40, unitWidth, unitHeight,
                "Alpha: ", cSource,
                (color) -> {
                    int initial = cSource.get();
                    EffectInfo.config().setCountdownColor(
                            setAlpha.applyAsInt(color)
                                    + setRed.applyAsInt(getRed.applyAsInt(initial))
                                    + setGreen.applyAsInt(getGreen.applyAsInt(initial))
                                    + setBlue.applyAsInt(getBlue.applyAsInt(initial)));
                },
                getAlpha, setAlpha));
        addRenderableWidget(new ColorSlider(blockX, blockY + 65, unitWidth, unitHeight,
                "Red: ", cSource,
                (color) -> {
                    int initial = cSource.get();
                    EffectInfo.config().setCountdownColor(
                            setAlpha.applyAsInt(getAlpha.applyAsInt(initial))
                                    + setRed.applyAsInt(color)
                                    + setGreen.applyAsInt(getGreen.applyAsInt(initial))
                                    + setBlue.applyAsInt(getBlue.applyAsInt(initial)));
                },
                getRed, setRed));
        addRenderableWidget(new ColorSlider(blockX, blockY + 90, unitWidth, unitHeight,
                "Green: ", cSource,
                (color) -> {
                    int initial = cSource.get();
                    EffectInfo.config().setCountdownColor(
                            setAlpha.applyAsInt(getAlpha.applyAsInt(initial))
                                    + setRed.applyAsInt(getRed.applyAsInt(initial))
                                    + setGreen.applyAsInt(color)
                                    + setBlue.applyAsInt(getBlue.applyAsInt(initial)));
                },
                getGreen, setGreen));

        ColorSlider slider = new ColorSlider(blockX, blockY + 115, unitWidth, unitHeight,
                "Blue: ", cSource,
                (color) -> {
                    int initial = cSource.get();
                    EffectInfo.config().setCountdownColor(
                            setAlpha.applyAsInt(getAlpha.applyAsInt(initial))
                                    + setRed.applyAsInt(getRed.applyAsInt(initial))
                                    + setGreen.applyAsInt(getGreen.applyAsInt(initial))
                                    + setBlue.applyAsInt(color));
                },
                getBlue, setBlue);
        addRenderableWidget(slider);


        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> onClose())
                .size(240, 20)
                .pos(width / 2 - 120, height - 27)
                .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        int x = width / 2 - 130;
        int y = height / 2 - 80;

        for (int i = 0; i < effectIcons.length; i++) {
            graphics.blitSprite(Gui.EFFECT_BACKGROUND_SPRITE, x, y, 24, 24);
            graphics.blit(x + 3, y + 3, 0, 18, 18, effectIcons[i]);
            graphics.drawString(minecraft.font, "II", x + 15, y + 3, EffectInfo.config().potencyColor, false);
            graphics.drawString(minecraft.font, "2:15", x + 3, y + 14, EffectInfo.config().countdownColor, false);
            x += 26;
        }
    }

    @Override
    public void onClose() {
        EffectInfo.config().writeChanges();
        super.onClose();
    }


    static class ColorSlider extends AbstractSliderButton {
        private final String label;
        private final Supplier<Integer> source;
        private final Consumer<Integer> dest;
        private final ToIntFunction<Integer> get;
        private final ToIntFunction<Integer> set;

        public ColorSlider(int x, int y, int width, int height, String label,
                           Supplier<Integer> source, Consumer<Integer> dest,
                           ToIntFunction<Integer> get, ToIntFunction<Integer> set) {
            super(x, y, width, height, Component.empty(), get.applyAsInt(source.get()) / 255d);
            this.label = label;
            this.source = source;
            this.dest = dest;
            this.get = get;
            this.set = set;
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            int value = get.applyAsInt(source.get());
            int color = set.applyAsInt(value);
            Component message = Component.literal(label)
                    .append(Component.literal(String.valueOf(value)))
                    .setStyle(Style.EMPTY.withColor(
                            TextColor.fromRgb(color >= 0 && color <= 16777215 ? color : 16777215)));
            this.setMessage(message);
        }

        @Override
        protected void applyValue() {
            dest.accept((int) (value * 255 + 0.5));
        }
    }
}
