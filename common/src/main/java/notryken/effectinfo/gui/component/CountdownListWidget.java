package notryken.effectinfo.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import notryken.effectinfo.EffectInfo;
import notryken.effectinfo.config.Config;
import notryken.effectinfo.gui.screen.ConfigScreen;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CountdownListWidget extends AbstractListWidget  {

    protected AbstractListWidget.Entry mainHeader;
    protected AbstractListWidget.Entry mainToggleButton;
    protected AbstractListWidget.Entry cornerButton;
    protected AbstractListWidget.Entry colorSelectionSet;
    protected AbstractListWidget.Entry alphaSlider;
    protected AbstractListWidget.Entry redSlider;
    protected AbstractListWidget.Entry greenSlider;
    protected AbstractListWidget.Entry blueSlider;
    protected AbstractListWidget.Entry warnHeader;
    protected AbstractListWidget.Entry warnToggleButton;
    protected AbstractListWidget.Entry warnColorSelectionSet;
    protected AbstractListWidget.Entry warnAlphaSlider;
    protected AbstractListWidget.Entry warnRedSlider;
    protected AbstractListWidget.Entry warnGreenSlider;
    protected AbstractListWidget.Entry warnBlueSlider;
    protected AbstractListWidget.Entry warnTimeSlider;
    protected AbstractListWidget.Entry resetButton;


    public CountdownListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight, ConfigScreen parent) {
        super(minecraft, width, height, y, itemHeight, parent);

        int unitWidth = 200;
        int unitHeight = 18;
        int unitX = parent.width - width + 10;

        Supplier<Integer> countdownSourceMain = EffectInfo.config()::getCountdownColor;
        Consumer<Integer> countdownDestMain = EffectInfo.config()::setCountdownColor;

        mainHeader = new AbstractListWidget.Entry.TextEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Countdown Text Options"));
        mainToggleButton = new AbstractListWidget.Entry.DualOnOffButtonEntry(this, unitX, 0,
                unitWidth, unitHeight,
                Component.literal("Display"), EffectInfo.config().countdownEnabled,
                (value) -> EffectInfo.config().countdownEnabled = value,
                Component.literal("Ambient"), EffectInfo.config().ambientCountdownEnabled,
                (value) -> EffectInfo.config().ambientCountdownEnabled = value);
        cornerButton = new AbstractListWidget.Entry.IntCycleButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Location"), EffectInfo.config().countdownLocation, (value) ->
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
                (value) -> EffectInfo.config().countdownLocation = value);
        colorSelectionSet = new AbstractListWidget.Entry.ColorSelectionSet(this, unitX, 0, unitWidth,
                (color) -> countdownDestMain.accept(withAlpha.applyAsInt(color, toAlpha.applyAsInt(countdownSourceMain.get()))));
        alphaSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Alpha: ", countdownSourceMain, (alpha) ->
                countdownDestMain.accept(withAlpha.applyAsInt(countdownSourceMain.get(), alpha)), toAlpha, fromAlpha);
        redSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Red: ", countdownSourceMain, (red) ->
                countdownDestMain.accept(withRed.applyAsInt(countdownSourceMain.get(), red)), toRed, fromRed);
        greenSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Green: ", countdownSourceMain, (green) ->
                countdownDestMain.accept(withGreen.applyAsInt(countdownSourceMain.get(), green)), toGreen, fromGreen);
        blueSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Blue: ", countdownSourceMain, (blue) ->
                countdownDestMain.accept(withBlue.applyAsInt(countdownSourceMain.get(), blue)), toBlue, fromBlue);

        Supplier<Integer> countdownSourceWarn = EffectInfo.config()::getWarnColor;
        Consumer<Integer> countdownDestWarn = EffectInfo.config()::setWarnColor;
        warnHeader = new AbstractListWidget.Entry.TextEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Low Time Warning Options"));
        warnToggleButton = new AbstractListWidget.Entry.DualOnOffButtonEntry(this, unitX, 0,
                unitWidth, unitHeight,
                Component.literal("Warn Color"), EffectInfo.config().countdownWarnEnabled,
                (value) -> EffectInfo.config().countdownWarnEnabled = value,
                Component.literal("Warn Flash"), EffectInfo.config().countdownFlashEnabled,
                (value) -> EffectInfo.config().countdownFlashEnabled = value);
        warnColorSelectionSet = new AbstractListWidget.Entry.ColorSelectionSet(this, unitX, 0, unitWidth,
                (color) -> countdownDestWarn.accept(withAlpha.applyAsInt(color, toAlpha.applyAsInt(countdownSourceWarn.get()))));
        warnAlphaSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Alpha: ", countdownSourceWarn, (alpha) ->
                countdownDestWarn.accept(withAlpha.applyAsInt(countdownSourceWarn.get(), alpha)), toAlpha, fromAlpha);
        warnRedSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Red: ", countdownSourceWarn, (red) ->
                countdownDestWarn.accept(withRed.applyAsInt(countdownSourceWarn.get(), red)), toRed, fromRed);
        warnGreenSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Green: ", countdownSourceWarn, (green) ->
                countdownDestWarn.accept(withGreen.applyAsInt(countdownSourceWarn.get(), green)), toGreen, fromGreen);
        warnBlueSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Blue: ", countdownSourceWarn, (blue) ->
                countdownDestWarn.accept(withBlue.applyAsInt(countdownSourceWarn.get(), blue)), toBlue, fromBlue);
        warnTimeSlider = new Entry.WarnTimeSlider(this, unitX, 0, unitWidth, unitHeight);
        resetButton = new AbstractListWidget.Entry.ActionButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Reset"),
                (button) -> {
                    EffectInfo.config().countdownEnabled = true;
                    EffectInfo.config().ambientCountdownEnabled = false;
                    EffectInfo.config().countdownLocation = Config.DEFAULT_COUNTDOWN_LOCATION;
                    EffectInfo.config().countdownColor = Config.DEFAULT_COLOR;
                    EffectInfo.config().countdownWarnEnabled = true;
                    EffectInfo.config().countdownFlashEnabled = true;
                    EffectInfo.config().warnColor = Config.DEFAULT_WARN_COLOR;
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
        public Entry(CountdownListWidget list) {
            super(list);
        }

        protected static class WarnTimeSlider extends Entry {

            public WarnTimeSlider(CountdownListWidget list, int x, int y, int width, int height) {
                super(list);

                IntSlider warningTimeSlider = new IntSlider(x, y, width, height,
                        120, "Warning Time: ", " sec",
                        EffectInfo.config()::getCountdownWarnTime,
                        (value) -> EffectInfo.config().setCountdownWarnTime(value));
                elements.add(warningTimeSlider);
            }
        }
    }
}
