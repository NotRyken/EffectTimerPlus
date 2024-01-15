package notryken.effectinfo.util;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import notryken.effectinfo.EffectInfo;

public class Util {
    public static int getColor(MobEffectInstance effectInstance) {
        int color = EffectInfo.config().countdownColor;
        if (EffectInfo.config().countdownWarnEnabled
                && effectInstance.getDuration() != MobEffectInstance.INFINITE_DURATION
                && effectInstance.getDuration() / 20 <= EffectInfo.config().countdownWarnTime
                && (!EffectInfo.config().countdownFlashEnabled
                || effectInstance.getDuration() % 20 >= 10)) {
            color = EffectInfo.config().getWarnColor();
        }
        return color;
    }

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
}
