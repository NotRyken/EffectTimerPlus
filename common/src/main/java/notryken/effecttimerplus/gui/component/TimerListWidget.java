package notryken.effecttimerplus.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import notryken.effecttimerplus.EffectTimerPlus;
import notryken.effecttimerplus.gui.screen.ConfigScreen;
import notryken.effecttimerplus.util.Util;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TimerListWidget extends AbstractListWidget  {

    protected AbstractListWidget.Entry mainHeader;
    protected AbstractListWidget.Entry mainToggleButton;
    protected AbstractListWidget.Entry cornerButton;
    protected AbstractListWidget.Entry colorSelectionSet;
    protected AbstractListWidget.Entry alphaSlider;
    protected AbstractListWidget.Entry redSlider;
    protected AbstractListWidget.Entry greenSlider;
    protected AbstractListWidget.Entry blueSlider;
    protected AbstractListWidget.Entry backAlphaSlider;
    protected AbstractListWidget.Entry warnHeader;
    protected AbstractListWidget.Entry warnToggleButton;
    protected AbstractListWidget.Entry warnColorSelectionSet;
    protected AbstractListWidget.Entry warnAlphaSlider;
    protected AbstractListWidget.Entry warnRedSlider;
    protected AbstractListWidget.Entry warnGreenSlider;
    protected AbstractListWidget.Entry warnBlueSlider;
    protected AbstractListWidget.Entry warnTimeSlider;
    protected AbstractListWidget.Entry resetButton;


    public TimerListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight, ConfigScreen parent) {
        super(minecraft, width, height, y, itemHeight, parent);

        int unitWidth = 200;
        int unitHeight = 18;
        int unitX = parent.width - width + 10;

        Supplier<Integer> colorSource = EffectTimerPlus.config()::getTimerColor;
        Consumer<Integer> colorDest = EffectTimerPlus.config()::setTimerColor;

        mainHeader = new AbstractListWidget.Entry.TextEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Timer Text Options"));
        mainToggleButton = new AbstractListWidget.Entry.DualOnOffButtonEntry(this, unitX, 0,
                unitWidth, unitHeight,
                Component.literal("Display"), EffectTimerPlus.config().timerEnabled,
                (value) -> EffectTimerPlus.config().timerEnabled = value,
                Component.literal("Ambient"), EffectTimerPlus.config().timerEnabledAmbient,
                (value) -> EffectTimerPlus.config().timerEnabledAmbient = value);
        cornerButton = new AbstractListWidget.Entry.IntCycleButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Location"), EffectTimerPlus.config().getTimerLocation(), (value) ->
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
                (value) -> EffectTimerPlus.config().setTimerLocation(value));
        colorSelectionSet = new AbstractListWidget.Entry.ColorSelectionSet(this, unitX, 0, unitWidth,
                (color) -> colorDest.accept(Util.withAlpha.applyAsInt(color, Util.toAlpha.applyAsInt(colorSource.get()))));
        alphaSlider = new AbstractListWidget.Entry.ArgbSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Opacity: ", colorSource, (alpha) ->
                colorDest.accept(Util.withAlpha.applyAsInt(colorSource.get(), alpha)), Util.toAlpha, Util.fromAlpha);
        redSlider = new AbstractListWidget.Entry.ArgbSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Red: ", colorSource, (red) ->
                colorDest.accept(Util.withRed.applyAsInt(colorSource.get(), red)), Util.toRed, Util.fromRed);
        greenSlider = new AbstractListWidget.Entry.ArgbSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Green: ", colorSource, (green) ->
                colorDest.accept(Util.withGreen.applyAsInt(colorSource.get(), green)), Util.toGreen, Util.fromGreen);
        blueSlider = new AbstractListWidget.Entry.ArgbSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Blue: ", colorSource, (blue) ->
                colorDest.accept(Util.withBlue.applyAsInt(colorSource.get(), blue)), Util.toBlue, Util.fromBlue);

        Supplier<Integer> backColorSource = EffectTimerPlus.config()::getTimerBackColor;
        Consumer<Integer> backColorDest = EffectTimerPlus.config()::setTimerBackColor;
        backAlphaSlider = new AbstractListWidget.Entry.ArgbSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Background Opacity: ", backColorSource, (alpha) ->
                backColorDest.accept(Util.withAlpha.applyAsInt(backColorSource.get(), alpha)), Util.toAlpha, Util.fromAlpha);

        Supplier<Integer> warnColorSource = EffectTimerPlus.config()::getTimerWarnColor;
        Consumer<Integer> warnColorDest = EffectTimerPlus.config()::setTimerWarnColor;
        warnHeader = new AbstractListWidget.Entry.TextEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Low Time Warning Options"));
        warnToggleButton = new AbstractListWidget.Entry.DualOnOffButtonEntry(this, unitX, 0,
                unitWidth, unitHeight,
                Component.literal("Warn Color"), EffectTimerPlus.config().timerWarnEnabled,
                (value) -> EffectTimerPlus.config().timerWarnEnabled = value,
                Component.literal("Warn Flash"), EffectTimerPlus.config().timerFlashEnabled,
                (value) -> EffectTimerPlus.config().timerFlashEnabled = value);
        warnColorSelectionSet = new AbstractListWidget.Entry.ColorSelectionSet(this, unitX, 0, unitWidth,
                (color) -> warnColorDest.accept(Util.withAlpha.applyAsInt(color, Util.toAlpha.applyAsInt(warnColorSource.get()))));
        warnAlphaSlider = new AbstractListWidget.Entry.ArgbSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Opacity: ", warnColorSource, (alpha) ->
                warnColorDest.accept(Util.withAlpha.applyAsInt(warnColorSource.get(), alpha)), Util.toAlpha, Util.fromAlpha);
        warnRedSlider = new AbstractListWidget.Entry.ArgbSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Red: ", warnColorSource, (red) ->
                warnColorDest.accept(Util.withRed.applyAsInt(warnColorSource.get(), red)), Util.toRed, Util.fromRed);
        warnGreenSlider = new AbstractListWidget.Entry.ArgbSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Green: ", warnColorSource, (green) ->
                warnColorDest.accept(Util.withGreen.applyAsInt(warnColorSource.get(), green)), Util.toGreen, Util.fromGreen);
        warnBlueSlider = new AbstractListWidget.Entry.ArgbSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Blue: ", warnColorSource, (blue) ->
                warnColorDest.accept(Util.withBlue.applyAsInt(warnColorSource.get(), blue)), Util.toBlue, Util.fromBlue);
        warnTimeSlider = new Entry.WarnTimeSlider(this, unitX, 0, unitWidth, unitHeight);

        resetButton = new AbstractListWidget.Entry.ActionButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Reset"),
                (button) -> {
                    EffectTimerPlus.config().resetTimerConfig();
                    reload();
                });

        addEntry(mainHeader);
        addEntry(mainToggleButton);
        addEntry(cornerButton);
        addEntry(colorSelectionSet);
        addEntry(alphaSlider);
        addEntry(redSlider);
        addEntry(greenSlider);
        addEntry(blueSlider);
        addEntry(backAlphaSlider);
        addEntry(warnHeader);
        addEntry(warnToggleButton);
        addEntry(warnColorSelectionSet);
        addEntry(warnAlphaSlider);
        addEntry(warnRedSlider);
        addEntry(warnGreenSlider);
        addEntry(warnBlueSlider);
        addEntry(warnTimeSlider);
        addEntry(resetButton);
    }

    protected abstract static class Entry extends AbstractListWidget.Entry {
        public Entry(TimerListWidget list) {
            super(list);
        }

        protected static class WarnTimeSlider extends Entry {

            public WarnTimeSlider(TimerListWidget list, int x, int y, int width, int height) {
                super(list);

                IntSlider warningTimeSlider = new IntSlider(x, y, width, height,
                        120, "Warning Time: ", " sec",
                        EffectTimerPlus.config()::getTimerWarnTime,
                        (value) -> EffectTimerPlus.config().setTimerWarnTime(value));
                elements.add(warningTimeSlider);
            }
        }
    }
}
