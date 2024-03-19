package notryken.effecttimerplus.gui.component.listwidget;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import notryken.effecttimerplus.EffectTimerPlus;
import notryken.effecttimerplus.gui.component.widget.DoubleSlider;
import notryken.effecttimerplus.gui.screen.ConfigScreen;
import notryken.effecttimerplus.util.MiscUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TimerListWidget extends AbstractListWidget {

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
    protected AbstractListWidget.Entry warnTimeSlider;
    protected AbstractListWidget.Entry warnColorSelectionSet;
    protected AbstractListWidget.Entry warnAlphaSlider;
    protected AbstractListWidget.Entry warnRedSlider;
    protected AbstractListWidget.Entry warnGreenSlider;
    protected AbstractListWidget.Entry warnBlueSlider;
    protected AbstractListWidget.Entry resetButton;


    public TimerListWidget(Minecraft minecraft, int width, int height, int top, int bottom,
                           int itemHeight, ConfigScreen parent) {
        super(minecraft, width, height, top, bottom, itemHeight, parent);

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
                Component.literal("Beacon"), EffectTimerPlus.config().timerEnabledAmbient,
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
                (color) -> colorDest.accept(MiscUtil.withAlpha.applyAsInt(color, MiscUtil.toAlpha.applyAsInt(colorSource.get()))));

        alphaSlider = new AbstractListWidget.Entry.ArgbSliderEntry2(this, unitX, unitWidth, unitHeight, "Opacity: ", colorSource,
                (color) -> colorDest.accept(MiscUtil.withAlpha.applyAsInt(colorSource.get(), color)),
                MiscUtil.toAlpha, MiscUtil.fromAlpha);
        redSlider = new AbstractListWidget.Entry.ArgbSliderEntry2(this, unitX, unitWidth, unitHeight, "Red: ", colorSource,
                (color) -> colorDest.accept(MiscUtil.withRed.applyAsInt(colorSource.get(), color)),
                MiscUtil.toRed, MiscUtil.fromRed);
        greenSlider = new AbstractListWidget.Entry.ArgbSliderEntry2(this, unitX, unitWidth, unitHeight, "Green: ", colorSource,
                (color) -> colorDest.accept(MiscUtil.withGreen.applyAsInt(colorSource.get(), color)),
                MiscUtil.toGreen, MiscUtil.fromGreen);
        blueSlider = new AbstractListWidget.Entry.ArgbSliderEntry2(this, unitX, unitWidth, unitHeight, "Blue: ", colorSource,
                (color) -> colorDest.accept(MiscUtil.withBlue.applyAsInt(colorSource.get(), color)),
                MiscUtil.toBlue, MiscUtil.fromBlue);

        Supplier<Integer> backColorSource = EffectTimerPlus.config()::getTimerBackColor;
        Consumer<Integer> backColorDest = EffectTimerPlus.config()::setTimerBackColor;
        backAlphaSlider = new AbstractListWidget.Entry.ArgbSliderEntry2(this, unitX, unitWidth, unitHeight, "Background Opacity: ", backColorSource,
                (color) -> backColorDest.accept(MiscUtil.withAlpha.applyAsInt(backColorSource.get(), color)),
                MiscUtil.toAlpha, MiscUtil.fromAlpha);

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
        warnTimeSlider = new Entry.WarnTimeSlider(this, unitX, 0, unitWidth, unitHeight);
        warnColorSelectionSet = new AbstractListWidget.Entry.ColorSelectionSet(this, unitX, 0, unitWidth,
                (color) -> warnColorDest.accept(MiscUtil.withAlpha.applyAsInt(color, MiscUtil.toAlpha.applyAsInt(warnColorSource.get()))));

        warnAlphaSlider = new AbstractListWidget.Entry.ArgbSliderEntry2(this, unitX, unitWidth, unitHeight, "Opacity: ", warnColorSource,
                (color) -> warnColorDest.accept(MiscUtil.withAlpha.applyAsInt(warnColorSource.get(), color)),
                MiscUtil.toAlpha, MiscUtil.fromAlpha);
        warnRedSlider = new AbstractListWidget.Entry.ArgbSliderEntry2(this, unitX, unitWidth, unitHeight, "Red: ", warnColorSource,
                (color) -> warnColorDest.accept(MiscUtil.withRed.applyAsInt(warnColorSource.get(), color)),
                MiscUtil.toRed, MiscUtil.fromRed);
        warnGreenSlider = new AbstractListWidget.Entry.ArgbSliderEntry2(this, unitX, unitWidth, unitHeight, "Green: ", warnColorSource,
                (color) -> warnColorDest.accept(MiscUtil.withGreen.applyAsInt(warnColorSource.get(), color)),
                MiscUtil.toGreen, MiscUtil.fromGreen);
        warnBlueSlider = new AbstractListWidget.Entry.ArgbSliderEntry2(this, unitX, unitWidth, unitHeight, "Blue: ", warnColorSource,
                (color) -> warnColorDest.accept(MiscUtil.withBlue.applyAsInt(warnColorSource.get(), color)),
                MiscUtil.toBlue, MiscUtil.fromBlue);

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
        addEntry(warnTimeSlider);
        addEntry(warnColorSelectionSet);
        addEntry(warnAlphaSlider);
        addEntry(warnRedSlider);
        addEntry(warnGreenSlider);
        addEntry(warnBlueSlider);
        addEntry(resetButton);
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 6 + x0;
    }

    protected abstract static class Entry extends AbstractListWidget.Entry {
        public Entry(TimerListWidget list) {
            super(list);
        }

        protected static class WarnTimeSlider extends Entry {

            public WarnTimeSlider(TimerListWidget list, int x, int y, int width, int height) {
                super(list);

                DoubleSlider warningTimeSlider = new DoubleSlider(x, y, width, height,
                        0, 120, 0, "Warning Time: ", " sec",
                        null, null,
                        () -> (double)EffectTimerPlus.config().getTimerWarnTime(),
                        (value) -> EffectTimerPlus.config().setTimerWarnTime(value.intValue()));
                elements.add(warningTimeSlider);
            }
        }
    }
}
