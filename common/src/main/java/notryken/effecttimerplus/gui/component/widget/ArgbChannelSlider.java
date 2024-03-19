package notryken.effecttimerplus.gui.component.widget;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import notryken.effecttimerplus.EffectTimerPlus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

/**
 * 0-255 slider for a channel (alpha, red, green or blue) of an ARGB color.
 * Slider message color will track the slider value.
 */
public class ArgbChannelSlider extends DoubleSlider {

    private final IntUnaryOperator toChannel;
    private final IntUnaryOperator fromChannel;

    /**
     * @param source argb color int source.
     * @param dest argb color int destination.
     * @param toChannel operator to get the value (0-255) of the slider's
     *                  channel from an ARGB int.
     * @param fromChannel operator convert the value (0-255) of the slider's
     *                    channel to an ARGB int.
     */
    public ArgbChannelSlider(int x, int y, int width, int height,
                             @Nullable String messagePrefix, @Nullable String messageSuffix,
                             Supplier<Integer> source, Consumer<Integer> dest,
                             IntUnaryOperator toChannel, IntUnaryOperator fromChannel) {
        super(x, y, width, height, 0, 255, 0, messagePrefix, messageSuffix,
                null, null,
                () -> (double) toChannel.applyAsInt(source.get()),
                (value) -> dest.accept(fromChannel.applyAsInt(value.intValue())));
        this.toChannel = toChannel;
        this.fromChannel = fromChannel;
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        int messageValue = (int)round(value * 255, 0);
        StringBuilder messageBuilder = new StringBuilder(String.valueOf(messageValue));
        if (messagePrefix != null) {
            messageBuilder.insert(0, messagePrefix);
        }
        if (messageSuffix != null) {
            messageBuilder.append(messageSuffix);
        }
        MutableComponent message = Component.literal(messageBuilder.toString());
        if (fromChannel != null) {
            int color = fromChannel.applyAsInt(messageValue);
            message = message.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(
                    (color >= 0 && color <= 16777215 ? color : 16777215))));
        }
        setMessage(message);
    }
}
