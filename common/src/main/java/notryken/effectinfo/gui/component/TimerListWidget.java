package notryken.effectinfo.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import notryken.effectinfo.EffectInfo;
import notryken.effectinfo.gui.screen.ConfigScreen;

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

        Supplier<Integer> colorSource = EffectInfo.config()::getTimerColor;
        Consumer<Integer> colorDest = EffectInfo.config()::setTimerColor;

        mainHeader = new AbstractListWidget.Entry.TextEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Timer Text Options"));
        mainToggleButton = new AbstractListWidget.Entry.DualOnOffButtonEntry(this, unitX, 0,
                unitWidth, unitHeight,
                Component.literal("Display"), EffectInfo.config().timerEnabled,
                (value) -> EffectInfo.config().timerEnabled = value,
                Component.literal("Ambient"), EffectInfo.config().timerEnabledAmbient,
                (value) -> EffectInfo.config().timerEnabledAmbient = value);
        cornerButton = new AbstractListWidget.Entry.IntCycleButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Location"), EffectInfo.config().getTimerLocation(), (value) ->
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
                (value) -> EffectInfo.config().setTimerLocation(value));
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

        Supplier<Integer> backColorSource = EffectInfo.config()::getTimerBackColor;
        Consumer<Integer> backColorDest = EffectInfo.config()::setTimerBackColor;
        backAlphaSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Background Opacity: ", backColorSource, (alpha) ->
                backColorDest.accept(withAlpha.applyAsInt(backColorSource.get(), alpha)), toAlpha, fromAlpha);

        Supplier<Integer> warnColorSource = EffectInfo.config()::getTimerWarnColor;
        Consumer<Integer> warnColorDest = EffectInfo.config()::setTimerWarnColor;
        warnHeader = new AbstractListWidget.Entry.TextEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Low Time Warning Options"));
        warnToggleButton = new AbstractListWidget.Entry.DualOnOffButtonEntry(this, unitX, 0,
                unitWidth, unitHeight,
                Component.literal("Warn Color"), EffectInfo.config().timerWarnEnabled,
                (value) -> EffectInfo.config().timerWarnEnabled = value,
                Component.literal("Warn Flash"), EffectInfo.config().timerFlashEnabled,
                (value) -> EffectInfo.config().timerFlashEnabled = value);
        warnColorSelectionSet = new AbstractListWidget.Entry.ColorSelectionSet(this, unitX, 0, unitWidth,
                (color) -> warnColorDest.accept(withAlpha.applyAsInt(color, toAlpha.applyAsInt(warnColorSource.get()))));
        warnAlphaSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Opacity: ", warnColorSource, (alpha) ->
                warnColorDest.accept(withAlpha.applyAsInt(warnColorSource.get(), alpha)), toAlpha, fromAlpha);
        warnRedSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Red: ", warnColorSource, (red) ->
                warnColorDest.accept(withRed.applyAsInt(warnColorSource.get(), red)), toRed, fromRed);
        warnGreenSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Green: ", warnColorSource, (green) ->
                warnColorDest.accept(withGreen.applyAsInt(warnColorSource.get(), green)), toGreen, fromGreen);
        warnBlueSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Blue: ", warnColorSource, (blue) ->
                warnColorDest.accept(withBlue.applyAsInt(warnColorSource.get(), blue)), toBlue, fromBlue);
        warnTimeSlider = new Entry.WarnTimeSlider(this, unitX, 0, unitWidth, unitHeight);

        resetButton = new AbstractListWidget.Entry.ActionButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Reset"),
                (button) -> {
                    EffectInfo.config().resetTimerConfig();
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
                        EffectInfo.config()::getTimerWarnTime,
                        (value) -> EffectInfo.config().setTimerWarnTime(value));
                elements.add(warningTimeSlider);
            }
        }
    }
}
