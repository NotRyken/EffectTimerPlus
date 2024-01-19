package notryken.effectinfo.gui.component;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

public class RgbaElementSlider extends AbstractSliderButton {
    private final String label;
    private final Supplier<Integer> source; // Slider value source function
    private final Consumer<Integer> dest; // Slider value destination function
    private final IntUnaryOperator toChannel;
    private final IntUnaryOperator fromChannel;

    public RgbaElementSlider(int x, int y, int width, int height, String label,
                             Supplier<Integer> source, Consumer<Integer> dest,
                             IntUnaryOperator toChannel, IntUnaryOperator fromChannel) {
        super(x, y, width, height, Component.empty(), toChannel.applyAsInt(source.get()) / 255d);
        this.label = label;
        this.source = source;
        this.dest = dest;
        this.toChannel = toChannel;
        this.fromChannel = fromChannel;
        updateMessage();
    }

    public void refresh() {
        value = toChannel.applyAsInt(source.get()) / 255d;
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        int scaledValue = (int)(value * 255 + 0.5);
        int color = fromChannel.applyAsInt(scaledValue);
        Component message = Component.literal(label)
                .append(Component.literal(String.valueOf(scaledValue)))
                .setStyle(Style.EMPTY.withColor(
                        TextColor.fromRgb(color >= 0 && color <= 16777215 ? color : 16777215)));
        this.setMessage(message);
    }

    @Override
    protected void applyValue() {
        dest.accept((int) (value * 255 + 0.5));
    }
}