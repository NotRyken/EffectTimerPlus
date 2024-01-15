package notryken.effectinfo.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import notryken.effectinfo.EffectInfo;
import notryken.effectinfo.config.Config;
import notryken.effectinfo.gui.screen.ConfigScreen;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PotencyListWidget extends AbstractListWidget  {

    protected AbstractListWidget.Entry header;
    protected AbstractListWidget.Entry toggleButton;
    protected AbstractListWidget.Entry cornerButton;
    protected AbstractListWidget.Entry colorSelectionSet;
    protected AbstractListWidget.Entry alphaSlider;
    protected AbstractListWidget.Entry redSlider;
    protected AbstractListWidget.Entry greenSlider;
    protected AbstractListWidget.Entry blueSlider;
    protected AbstractListWidget.Entry bgAlphaSlider;
    protected AbstractListWidget.Entry resetButton;


    public PotencyListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight, ConfigScreen parent) {
        super(minecraft, width, height, y, itemHeight, parent);

        int unitWidth = 200;
        int unitHeight = 18;
        int unitX = width - unitWidth - 10;

        Supplier<Integer> colorSource = EffectInfo.config()::getPotencyColor;
        Consumer<Integer> colorDest = EffectInfo.config()::setPotencyColor;

        header = new AbstractListWidget.Entry.TextEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Potency Text Options"));
        toggleButton = new AbstractListWidget.Entry.OnOffButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Display"), EffectInfo.config().potencyEnabled,
                (value) -> EffectInfo.config().potencyEnabled = value);
        cornerButton = new AbstractListWidget.Entry.IntCycleButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Location"), EffectInfo.config().potencyLocation, (value) ->
                switch(value) {
                    case 0:
                        yield Component.literal("Top Left");
                    case 1:
                        yield Component.literal("Top Right");
                    case 2:
                        yield Component.literal("Bottom Left");
                    case 3:
                        yield Component.literal("Bottom Right");
                    default:
                        throw new IllegalStateException("Unexpected value: " + value);
                },
                new Integer[]{0, 1, 3, 2},
                (value) -> EffectInfo.config().potencyLocation = value);
        colorSelectionSet = new AbstractListWidget.Entry.ColorSelectionSet(this, unitX, 0, unitWidth,
                (color) -> colorDest.accept(withAlpha.applyAsInt(color, toAlpha.applyAsInt(colorSource.get()))));
        alphaSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Alpha: ", colorSource, (alpha) ->
                colorDest.accept(withAlpha.applyAsInt(colorSource.get(), alpha)), toAlpha, fromAlpha);
        redSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Red: ", colorSource, (red) ->
                colorDest.accept(withRed.applyAsInt(colorSource.get(), red)), toRed, fromRed);
        greenSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Green: ", colorSource, (green) ->
                colorDest.accept(withGreen.applyAsInt(colorSource.get(), green)), toGreen, fromGreen);
        blueSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Blue: ", colorSource, (blue) ->
                colorDest.accept(withBlue.applyAsInt(colorSource.get(), blue)), toBlue, fromBlue);

        Supplier<Integer> bgColorSource = EffectInfo.config()::getPotencyBgColor;
        Consumer<Integer> bgColorDest = EffectInfo.config()::setPotencyBgColor;
        bgAlphaSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Background Alpha: ", bgColorSource, (alpha) ->
                bgColorDest.accept(withAlpha.applyAsInt(bgColorSource.get(), alpha)), toAlpha, fromAlpha);

        resetButton = new AbstractListWidget.Entry.ActionButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Reset"),
                (button) -> {
                    EffectInfo.config().potencyEnabled = true;
                    EffectInfo.config().potencyLocation = Config.DEFAULT_POTENCY_LOCATION;
                    EffectInfo.config().potencyColor = Config.DEFAULT_COLOR;
                    EffectInfo.config().potencyBgColor = Config.DEFAULT_BG_COLOR;
                    reload();
                });

        addEntry(header);
        addEntry(toggleButton);
        addEntry(cornerButton);
        addEntry(colorSelectionSet);
        addEntry(alphaSlider);
        addEntry(redSlider);
        addEntry(greenSlider);
        addEntry(blueSlider);
        addEntry(bgAlphaSlider);
        addEntry(resetButton);
    }

    protected abstract static class Entry extends AbstractListWidget.Entry {
        public Entry(PotencyListWidget list) {
            super(list);
        }
    }
}
