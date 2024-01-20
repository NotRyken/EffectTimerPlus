package notryken.effecttimerplus.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import notryken.effecttimerplus.EffectTimerPlus;
import notryken.effecttimerplus.gui.screen.ConfigScreen;
import notryken.effecttimerplus.util.Util;

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


    public PotencyListWidget(Minecraft minecraft, int width, int height, int y,
                             int itemHeight, ConfigScreen parent) {
        super(minecraft, width, height, y, itemHeight, parent);

        int unitWidth = 200;
        int unitHeight = 18;
        int unitX = width - unitWidth - 10;

        Supplier<Integer> colorSource = EffectTimerPlus.config()::getPotencyColor;
        Consumer<Integer> colorDest = EffectTimerPlus.config()::setPotencyColor;

        header = new AbstractListWidget.Entry.TextEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Potency Text Options"));
        toggleButton = new AbstractListWidget.Entry.OnOffButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Display"), EffectTimerPlus.config().potencyEnabled,
                (value) -> EffectTimerPlus.config().potencyEnabled = value);
        cornerButton = new AbstractListWidget.Entry.IntCycleButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Location"), EffectTimerPlus.config().getPotencyLocation(), (value) ->
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
                (value) -> EffectTimerPlus.config().setPotencyLocation(value));
        colorSelectionSet = new AbstractListWidget.Entry.ColorSelectionSet(this, unitX, 0, unitWidth,
                (color) -> colorDest.accept(Util.withAlpha.applyAsInt(color, Util.toAlpha.applyAsInt(colorSource.get()))));
        alphaSlider = new Entry.ArgbSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Opacity: ", colorSource, (alpha) ->
                colorDest.accept(Util.withAlpha.applyAsInt(colorSource.get(), alpha)), Util.toAlpha, Util.fromAlpha);
        redSlider = new Entry.ArgbSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Red: ", colorSource, (red) ->
                colorDest.accept(Util.withRed.applyAsInt(colorSource.get(), red)), Util.toRed, Util.fromRed);
        greenSlider = new Entry.ArgbSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Green: ", colorSource, (green) ->
                colorDest.accept(Util.withGreen.applyAsInt(colorSource.get(), green)), Util.toGreen, Util.fromGreen);
        blueSlider = new Entry.ArgbSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Blue: ", colorSource, (blue) ->
                colorDest.accept(Util.withBlue.applyAsInt(colorSource.get(), blue)), Util.toBlue, Util.fromBlue);

        Supplier<Integer> backColorSource = EffectTimerPlus.config()::getPotencyBackColor;
        Consumer<Integer> backColorDest = EffectTimerPlus.config()::setPotencyBackColor;
        backAlphaSlider = new Entry.ArgbSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Background Opacity: ", backColorSource, (alpha) ->
                backColorDest.accept(Util.withAlpha.applyAsInt(backColorSource.get(), alpha)), Util.toAlpha, Util.fromAlpha);

        resetButton = new AbstractListWidget.Entry.ActionButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Reset"),
                (button) -> {
                    EffectTimerPlus.config().resetPotencyConfig();
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

    @Override
    protected int getScrollbarPosition() {
        return this.width - 6;
    }
}
