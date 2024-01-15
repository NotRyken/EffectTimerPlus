package notryken.effectinfo.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import notryken.effectinfo.gui.screen.ConfigScreen;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

public abstract class AbstractListWidget extends ContainerObjectSelectionList<AbstractListWidget.Entry> {
    // RGBA color utils
    protected final IntUnaryOperator toAlpha = (value) -> (value >> 24 & 255);
    protected final IntUnaryOperator toRed = (value) -> (value >> 16 & 255);
    protected final IntUnaryOperator toGreen = (value) -> (value >> 8 & 255);
    protected final IntUnaryOperator toBlue = (value) -> (value & 255);
    protected final IntUnaryOperator fromAlpha = (value) -> (value * 16777216);
    protected final IntUnaryOperator fromRed = (value) -> (value * 65536);
    protected final IntUnaryOperator fromGreen = (value) -> (value * 256);
    protected final IntUnaryOperator fromBlue = (value) -> (value);
    protected final IntBinaryOperator withAlpha = (value, alpha) ->
            ((alpha * 16777216) + value - (value >> 24 & 255) * 16777216);
    protected final IntBinaryOperator withRed = (value, red) ->
            ((red * 65536) + value - (value >> 16 & 255) * 65536);
    protected final IntBinaryOperator withGreen = (value, green) ->
            ((green * 256) + value - (value >> 8 & 255) * 256);
    protected final IntBinaryOperator withBlue = (value, blue) ->
            ((blue) + value - (value & 255));

    protected final ConfigScreen parent;

    public AbstractListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight,
                              ConfigScreen parent) {
        super(minecraft, width, height, y, itemHeight);
        this.parent = parent;
    }

    @Override
    public int getRowWidth() {
        return this.width - 10;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 6 + this.getX();
    }

    protected void reload() {
        parent.reload();
    }

    protected abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        protected final Minecraft minecraft;
        protected final AbstractListWidget list;
        protected final List<AbstractWidget> elements;

        public Entry(AbstractListWidget list) {
            this.minecraft = Minecraft.getInstance();
            this.list = list;
            this.elements = new ArrayList<>();
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return elements;
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return elements;
        }

        @Override
        public void render(@NotNull GuiGraphics graphics, int index, int y, int x,
                           int entryWidth, int entryHeight, int mouseX, int mouseY,
                           boolean hovered, float tickDelta) {
            elements.forEach((element) -> {
                element.setY(y);
                element.render(graphics, mouseX, mouseY, tickDelta);
            });
        }

        protected static class TextEntry extends Entry {
            public TextEntry(AbstractListWidget list, int x, int y, int width, int height,
                             Component message, Tooltip... tooltip) {
                super(list);
                StringWidget textEntry = new StringWidget(x, y, width, height, message,
                        Minecraft.getInstance().font);
                if (tooltip.length == 1) {
                    textEntry.setTooltip(tooltip[0]);
                }
                elements.add(textEntry);
            }
        }

        protected static class ActionButtonEntry extends Entry {
            public ActionButtonEntry(AbstractListWidget list, int x, int y, int width, int height,
                                     Component label, Button.OnPress onPress) {
                super(list);
                Button actionButton = Button.builder(label, onPress)
                        .pos(x, y)
                        .size(width, height)
                        .build();
                elements.add(actionButton);
            }
        }

        protected static class OnOffButtonEntry extends Entry {
            public OnOffButtonEntry(AbstractListWidget list, int x, int y, int width, int height,
                                    Component label, boolean initial, Consumer<Boolean> dest) {
                super(list);
                CycleButton<Boolean> cycleButton = CycleButton.onOffBuilder(initial)
                        .create(x, y, width, height, label, (button, status) -> dest.accept(status));
                elements.add(cycleButton);
            }
        }

        protected static class DualOnOffButtonEntry extends Entry {
            public DualOnOffButtonEntry(AbstractListWidget list, int x, int y, int width, int height,
                                        Component leftLabel, boolean leftInitial, Consumer<Boolean> leftDest,
                                        Component rightLabel, boolean rightInitial, Consumer<Boolean> rightDest) {
                super(list);
                CycleButton<Boolean> leftCycleButton = CycleButton.onOffBuilder(leftInitial)
                        .create(x, y, width / 2 - 2, height, leftLabel, (button, status) -> leftDest.accept(status));
                CycleButton<Boolean> rightCycleButton = CycleButton.onOffBuilder(rightInitial)
                        .create(x + width / 2 + 2, y, width / 2 - 2, height, rightLabel, (button, status) -> rightDest.accept(status));
                elements.add(leftCycleButton);
                elements.add(rightCycleButton);
            }
        }

        protected static class IntCycleButtonEntry extends Entry {
            public IntCycleButtonEntry(AbstractListWidget list, int x, int y, int width, int height,
                                       Component label, int initial, Function<Integer,Component> intToText,
                                       Integer[] values, Consumer<Integer> dest) {
                super(list);
                CycleButton<Integer> cycleButton = CycleButton.builder(intToText)
                        .withInitialValue(initial)
                        .withValues(values)
                        .create(x, y, width, height, label, (button, value) -> dest.accept(value));
                elements.add(cycleButton);
            }
        }

        protected static class RgbaSliderEntry extends Entry {
            public RgbaSliderEntry(AbstractListWidget list, int x, int y, int width, int height, String label,
                                   Supplier<Integer>source, Consumer<Integer> dest,
                                   IntUnaryOperator toChannel, IntUnaryOperator fromChannel) {
                super(list);
                RgbaElementSlider rgbaSlider = new RgbaElementSlider(x, y, width, height, label,
                        source, dest, toChannel, fromChannel);
                elements.add(rgbaSlider);
            }
        }

        protected static class ColorSelectionSet extends Entry {
            int[] quickColors = new int[] {
                    10027008,
                    16711680,
                    16753920,
                    16761856,
                    16776960,
                    65280,
                    32768,
                    19456,
                    2142890,
                    65535,
                    255,
                    8388736,
                    16711935,
                    16777215,
                    8421504,
                    0};

            public ColorSelectionSet(AbstractListWidget list, int x, int y, int width, Consumer<Integer> dest) {
                super(list);

                int buttonWidth = width / quickColors.length;
                for (int i = 0; i < quickColors.length; i++) {
                    int color = quickColors[i];
                    int setX = x + (width / 2) - (buttonWidth * quickColors.length / 2);
                    elements.add(Button.builder(Component.literal("\u2588")
                                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color))), (button) ->
                            {
                                dest.accept(color);
                                list.reload();
                                // FIXME or refresh possibly need list of entries in AbstractListWidget
                            })
                            .pos(setX + (buttonWidth * i), y)
                            .size(buttonWidth, buttonWidth)
                            .build());
                }
            }
        }
    }
}
