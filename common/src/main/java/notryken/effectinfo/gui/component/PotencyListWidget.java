package notryken.effectinfo.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import notryken.effectinfo.EffectInfo;
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
    protected AbstractListWidget.Entry backAlphaSlider;
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
                Component.literal("Location"), EffectInfo.config().getPotencyLocation(), (value) ->
                switch(value) {
                    case 0:
                        yield Component.literal("Top Left");
                    case 1:
                        yield Component.literal("Top Center");
                    case 2:
                        yield Component.literal("Top Right");
                    case 3:
                        yield Component.literal("Center Right");
                    case 4:
                        yield Component.literal("Bottom Right");
                    case 5:
                        yield Component.literal("Bottom Center");
                    case 6:
                        yield Component.literal("Bottom Left");
                    case 7:
                        yield Component.literal("Center Left");
                    default:
                        throw new IllegalStateException(
                                "Unexpected positional index outside of allowed range (0-7): " + value);
                },
                new Integer[]{0, 1, 2, 3, 4, 5, 6, 7},
                (value) -> EffectInfo.config().setPotencyLocation(value));
        colorSelectionSet = new AbstractListWidget.Entry.ColorSelectionSet(this, unitX, 0, unitWidth,
                (color) -> colorDest.accept(withAlpha.applyAsInt(color, toAlpha.applyAsInt(colorSource.get()))));
        alphaSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Opacity: ", colorSource, (alpha) ->
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

        Supplier<Integer> backColorSource = EffectInfo.config()::getPotencyBackColor;
        Consumer<Integer> backColorDest = EffectInfo.config()::setPotencyBackColor;
        backAlphaSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Background Opacity: ", backColorSource, (alpha) ->
                backColorDest.accept(withAlpha.applyAsInt(backColorSource.get(), alpha)), toAlpha, fromAlpha);

        resetButton = new AbstractListWidget.Entry.ActionButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Reset"),
                (button) -> {
                    EffectInfo.config().resetPotencyConfig();
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
        addEntry(backAlphaSlider);
        addEntry(resetButton);
    }
}
