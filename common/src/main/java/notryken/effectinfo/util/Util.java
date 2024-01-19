package notryken.effectinfo.util;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.effect.MobEffectInstance;
import notryken.effectinfo.EffectInfo;

public class Util {

    /**
     * @param effectInstance the {@code MobEffectInstance} to get color for.
     * @return the color as defined by mod configuration.
     */
    public static int getTimerColor(MobEffectInstance effectInstance) {
        int color = EffectInfo.config().getTimerColor();
        if (EffectInfo.config().timerWarnEnabled
                && effectInstance.getDuration() != MobEffectInstance.INFINITE_DURATION
                && effectInstance.getDuration() / 20 <= EffectInfo.config().getTimerWarnTime()
                && (!EffectInfo.config().timerFlashEnabled
                || effectInstance.getDuration() % 20 >= 10)) {
            color = EffectInfo.config().getTimerWarnColor();
        }
        return color;
    }

    /**
     * Converts an amplifier number into a Roman numeral {@code String}, if
     * possible.
     * @param amplifier the amplifier number.
     * @return a {@code String} representing the number, or an empty
     * {@code String} if the number is invalid.
     */
    public static String getAmplifierAsString(int amplifier) {
        int value = amplifier + 1;
        if (value > 1) {
            String key = String.format("enchantment.level.%d", value);
            if (I18n.exists(key)) {
                return I18n.get(key);
            } else {
                return String.valueOf(value);
            }
        }
        return "";
    }

    /**
     * Converts a duration in ticks to a readable approximation.
     * @param durationTicks the duration, in ticks.
     * @return a {@code String} representing the duration.
     */
    public static String getDurationAsString(int durationTicks) {
        if(durationTicks == MobEffectInstance.INFINITE_DURATION) {
            return "\u221e";
        }
        int seconds = durationTicks / 20;
        if (seconds >= 360000) { // 100 hours
            return "\u221e";
        }
        else if (seconds >= 3600) {
            return seconds / 3600 + "h";
        }
        else if (seconds >= 60) {
            return seconds / 60 + "m";
        }
        else {
            return String.valueOf(seconds);
        }
    }

    /**
     * Determines the X offset for a {@code String} of the given width to be
     * drawn over a status effect icon, based on the given positional index.
     * @param locIndex the positional index (0-7).
     * @param labelWidth the width of the {@code String} to be rendered.
     * @return the X-axis offset.
     * @throws IllegalStateException if the given index is invalid.
     */
    public static int getTextOffsetX(int locIndex, int labelWidth) {
        return switch (locIndex) {
            case 0, 6, 7 -> 3;
            case 1, 5 -> 13 - labelWidth / 2;
            case 2, 3, 4 -> 22 - labelWidth;
            default -> throw new IllegalStateException(
                    "Unexpected positional index outside of allowed range (0-7): " + locIndex);
        };
    }

    /**
     * Determines the Y offset for a {@code String} to be drawn over a status
     * effect icon, based on the given positional index.
     * @param locIndex the positional index (0-7).
     * @return the Y-axis offset.
     * @throws IllegalStateException if the given index is invalid.
     */
    public static int getTextOffsetY(int locIndex) {
        return switch (locIndex) {
            case 0, 1, 2 -> 3;
            case 3, 7 -> 9;
            case 4, 5, 6 -> 14;
            default -> throw new IllegalStateException(
                    "Unexpected positional index outside of allowed range (0-7): " + locIndex);
        };
    }
}
