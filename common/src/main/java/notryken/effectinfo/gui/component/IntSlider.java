package notryken.effectinfo.gui.component;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IntSlider extends AbstractSliderButton {
    private final int max;
    private final String labelPrefix;
    private final String labelSuffix;
    private final Supplier<Integer> source; // Slider value source function
    private final Consumer<Integer> dest; // Slider value destination function

    public IntSlider(int x, int y, int width, int height, int max, String labelPrefix, String labelSuffix,
                     Supplier<Integer> source, Consumer<Integer> dest) {
        super(x, y, width, height, Component.empty(), source.get() / (double)max);
        this.max = max;
        this.labelPrefix = labelPrefix;
        this.labelSuffix = labelSuffix;
        this.source = source;
        this.dest = dest;
        updateMessage();
    }

    public void refresh() {
        value = source.get() / (double)max;
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        int scaledValue = (int)(value * max + 0.5);
        setMessage(Component.literal(labelPrefix + scaledValue + labelSuffix));
    }

    @Override
    protected void applyValue() {
        dest.accept((int)(value * max + 0.5));
    }
}
