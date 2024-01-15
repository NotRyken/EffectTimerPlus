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
    protected AbstractListWidget.Entry resetButton;


    public PotencyListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight, ConfigScreen parent) {
        super(minecraft, width, height, y, itemHeight, parent);

        int unitWidth = 200;
        int unitHeight = 18;
        int unitX = width - unitWidth - 10;

        Supplier<Integer> potencySource = EffectInfo.config()::getPotencyColor;
        Consumer<Integer> potencyDest = EffectInfo.config()::setPotencyColor;

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
                (color) -> potencyDest.accept(withAlpha.applyAsInt(color, toAlpha.applyAsInt(potencySource.get()))));
        alphaSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Alpha: ", potencySource, (alpha) ->
                potencyDest.accept(withAlpha.applyAsInt(potencySource.get(), alpha)), toAlpha, fromAlpha);
        redSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Red: ", potencySource, (red) ->
                potencyDest.accept(withRed.applyAsInt(potencySource.get(), red)), toRed, fromRed);
        greenSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Green: ", potencySource, (green) ->
                potencyDest.accept(withGreen.applyAsInt(potencySource.get(), green)), toGreen, fromGreen);
        blueSlider = new AbstractListWidget.Entry.RgbaSliderEntry(this, unitX, 0, unitWidth, unitHeight,
                "Blue: ", potencySource, (blue) ->
                potencyDest.accept(withBlue.applyAsInt(potencySource.get(), blue)), toBlue, fromBlue);
        resetButton = new AbstractListWidget.Entry.ActionButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Reset"),
                (button) -> {
                    EffectInfo.config().potencyEnabled = true;
                    EffectInfo.config().potencyLocation = Config.DEFAULT_POTENCY_LOCATION;
                    EffectInfo.config().potencyColor = Config.DEFAULT_COLOR;
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
        addEntry(resetButton);
    }

    protected abstract static class Entry extends AbstractListWidget.Entry {
        public Entry(PotencyListWidget list) {
            super(list);
        }
    }
}
