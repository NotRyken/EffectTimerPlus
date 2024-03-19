package notryken.effecttimerplus.gui.component.listwidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import notryken.effecttimerplus.gui.component.widget.ArgbChannelSlider;
import notryken.effecttimerplus.gui.screen.ConfigScreen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

/**
 * Abstract list widget used to factor out common code between
 * {@code PotencyListWidget} and {@code TimerListWidget}.
 */
public abstract class AbstractListWidget extends ContainerObjectSelectionList<AbstractListWidget.Entry> {

    protected final ConfigScreen parent;

    public AbstractListWidget(Minecraft minecraft, int width, int height, int top, int bottom,
                              int itemHeight, ConfigScreen parent) {
        super(minecraft, width, height, top, bottom, itemHeight);
        this.parent = parent;
    }

    protected void reload() {
        parent.reload();
    }

    @Override
    public int getRowWidth() {
        return this.width - 15;
    }

    /**
     * List entries used by {@code PotencyListWidget} and
     * {@code TimerListWidget}.
     */
    protected abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        protected final AbstractListWidget list;
        protected final List<AbstractWidget> elements;

        public Entry(AbstractListWidget list) {
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

        protected static class ArgbSliderEntry2 extends Entry {
            ArgbChannelSlider slider;
            public ArgbSliderEntry2(AbstractListWidget list, int x, int width, int height, @Nullable String message,
                                  Supplier<Integer> source, Consumer<Integer> dest,
                                  IntUnaryOperator toChannel, IntUnaryOperator fromChannel) {
                super(list);
                slider = new ArgbChannelSlider(x, 0, width, height, message, null,
                        source, dest, toChannel, fromChannel);
                elements.add(slider);
            }

            public void refresh() {
                slider.refresh();
            }
        }

        protected static class ColorSelectionSet extends Entry {
            int[] colors = new int[] {
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

                int buttonWidth = width / colors.length;
                for (int i = 0; i < colors.length; i++) {
                    int color = colors[i];
                    int setX = x + (width / 2) - (buttonWidth * colors.length / 2);
                    elements.add(Button.builder(Component.literal("\u2588")
                                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color))), (button) ->
                            {
                                dest.accept(color);
                                for (Entry entry : list.children()) {
                                    if (entry instanceof ArgbSliderEntry2 sliderEntry) {
                                        sliderEntry.refresh();
                                    }
                                }
                            })
                            .pos(setX + (buttonWidth * i), y)
                            .size(buttonWidth, buttonWidth)
                            .build());
                }
            }
        }
    }
}
