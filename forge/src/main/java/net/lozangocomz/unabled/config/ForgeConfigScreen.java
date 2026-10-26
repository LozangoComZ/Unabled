package net.lozangocomz.unabled.config;

import net.lozangocomz.unabled.Config;
import net.lozangocomz.unabled.Settings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;

/** Client configuration UI for Forge's server-scoped settings. */
public final class ForgeConfigScreen extends Screen {
    private static final int ROW_HEIGHT = 68;
    private static final String[] OPTION_KEYS = {
            "damage_mode", "damage_player", "unequipped_mode", "dig_tools_mode",
            "required_tools_mode", "breaking_speed_threshold", "ignored_ids"
    };

    private final Screen parent;
    private Settings values;
    private EditBox ignoredIds;
    private boolean editable;
    private int page;
    private int rowsPerPage;
    private int panelLeft;
    private int panelWidth;
    private int contentTop;
    private int sliderWidth;

    public ForgeConfigScreen(Screen parent) {
        this(parent, null, 0);
    }

    private ForgeConfigScreen(Screen parent, Settings values, int page) {
        super(Component.translatable("unabled.config.title"));
        this.parent = parent;
        this.values = values;
        this.page = page;
    }

    @Override
    protected void init() {
        editable = ForgeConfig.isLoaded() && minecraft.hasSingleplayerServer();
        if (values == null) {
            values = ForgeConfig.isLoaded() ? Config.get() : Settings.defaults();
        }
        panelWidth = Math.max(220, Math.min(560, width - 16));
        panelLeft = (width - panelWidth) / 2;
        contentTop = 38;
        rowsPerPage = Math.max(1, Math.min(OPTION_KEYS.length, (height - 95) / ROW_HEIGHT));
        int pageCount = pageCount();
        page = Math.max(0, Math.min(page, pageCount - 1));
        sliderWidth = Math.max(150, Math.min(280, panelWidth - 64));

        int firstOption = page * rowsPerPage;
        int lastOption = Math.min(OPTION_KEYS.length, firstOption + rowsPerPage);
        for (int option = firstOption; option < lastOption; option++) {
            int y = contentTop + (option - firstOption) * ROW_HEIGHT;
            if (option == 6) {
                addIgnoredIdsField(y);
            } else {
                addSlider(option, y);
            }
        }

        addNavigationButtons(pageCount);
    }

    private void addSlider(int option, int y) {
        int x = width / 2 - sliderWidth / 2;
        AbstractSliderButton slider = switch (option) {
            case 0 -> new ModeSlider(x, y + 39, sliderWidth, 20, values.damageMode(), value -> values = new Settings(
                    value, values.damagePlayer(), values.unequippedMode(), values.digToolsMode(), values.requiredToolsMode(),
                    values.breakingSpeedThreshold(), values.ignoredIds()));
            case 1 -> new DoubleSlider(x, y + 39, sliderWidth, 20, values.damagePlayer(), 0.0, 20.0, 0.1,
                    value -> values = new Settings(values.damageMode(), value, values.unequippedMode(), values.digToolsMode(),
                            values.requiredToolsMode(), values.breakingSpeedThreshold(), values.ignoredIds()));
            case 2 -> new ModeSlider(x, y + 39, sliderWidth, 20, values.unequippedMode(), value -> values = new Settings(
                    values.damageMode(), values.damagePlayer(), value, values.digToolsMode(), values.requiredToolsMode(),
                    values.breakingSpeedThreshold(), values.ignoredIds()));
            case 3 -> new ModeSlider(x, y + 39, sliderWidth, 20, values.digToolsMode(), value -> values = new Settings(
                    values.damageMode(), values.damagePlayer(), values.unequippedMode(), value, values.requiredToolsMode(),
                    values.breakingSpeedThreshold(), values.ignoredIds()));
            case 4 -> new ModeSlider(x, y + 39, sliderWidth, 20, values.requiredToolsMode(), value -> values = new Settings(
                    values.damageMode(), values.damagePlayer(), values.unequippedMode(), values.digToolsMode(), value,
                    values.breakingSpeedThreshold(), values.ignoredIds()));
            case 5 -> new DoubleSlider(x, y + 39, sliderWidth, 20, values.breakingSpeedThreshold(), 0.0, 20.0, 0.25,
                    value -> values = new Settings(values.damageMode(), values.damagePlayer(), values.unequippedMode(),
                            values.digToolsMode(), values.requiredToolsMode(), value, values.ignoredIds()));
            default -> throw new IllegalArgumentException("Unknown config option: " + option);
        };
        slider.active = editable;
        addRenderableWidget(slider);
    }

    private void addIgnoredIdsField(int y) {
        ignoredIds = new EditBox(font, panelLeft + 8, y + 39, panelWidth - 16, 20,
                Component.translatable("unabled.config.ignored_id"));
        ignoredIds.setMaxLength(2048);
        ignoredIds.setValue(String.join(", ", values.ignoredIds()));
        ignoredIds.active = editable;
        ignoredIds.setEditable(editable);
        addRenderableWidget(ignoredIds);
    }

    private void addNavigationButtons(int pageCount) {
        int buttonY = height - 27;
        int buttonWidth = 72;
        int gap = 6;
        int totalWidth = buttonWidth * 4 + gap * 3;
        int x = width / 2 - totalWidth / 2;

        Button previous = Button.builder(Component.translatable("unabled.config.previous"), button -> changePage(page - 1))
                .bounds(x, buttonY, buttonWidth, 20).build();
        previous.active = page > 0;
        addRenderableWidget(previous);

        Button save = Button.builder(Component.translatable("gui.done"), button -> saveAndClose())
                .bounds(x + buttonWidth + gap, buttonY, buttonWidth, 20).build();
        save.active = editable;
        addRenderableWidget(save);

        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> onClose())
                .bounds(x + (buttonWidth + gap) * 2, buttonY, buttonWidth, 20).build());

        Button next = Button.builder(Component.translatable("unabled.config.next"), button -> changePage(page + 1))
                .bounds(x + (buttonWidth + gap) * 3, buttonY, buttonWidth, 20).build();
        next.active = page + 1 < pageCount;
        addRenderableWidget(next);
    }

    private int pageCount() {
        return (OPTION_KEYS.length + rowsPerPage - 1) / rowsPerPage;
    }

    private void changePage(int newPage) {
        Settings currentValues = values;
        if (ignoredIds != null) {
            currentValues = withIgnoredIds(currentValues, ignoredIds.getValue());
        }
        minecraft.setScreen(new ForgeConfigScreen(parent, currentValues, newPage));
    }

    private void saveAndClose() {
        if (!editable || !ForgeConfig.isLoaded() || !minecraft.hasSingleplayerServer()) {
            onClose();
            return;
        }
        ForgeConfig.save(ignoredIds == null ? values : withIgnoredIds(values, ignoredIds.getValue()));
        onClose();
    }

    private static Settings withIgnoredIds(Settings source, String text) {
        List<String> ignored = Arrays.stream(text.split(","))
                .map(String::trim).filter(value -> !value.isEmpty()).toList();
        return new Settings(source.damageMode(), source.damagePlayer(), source.unequippedMode(), source.digToolsMode(),
                source.requiredToolsMode(), source.breakingSpeedThreshold(), ignored);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.fill(panelLeft, 4, panelLeft + panelWidth, height - 4, 0xC0101010);
        graphics.drawCenteredString(font, title, width / 2, 10, 0xFFFFFF);
        graphics.drawCenteredString(font, Component.translatable("unabled.config.page", page + 1, pageCount()),
                width / 2, 22, 0xA0A0A0);

        int firstOption = page * rowsPerPage;
        int lastOption = Math.min(OPTION_KEYS.length, firstOption + rowsPerPage);
        for (int option = firstOption; option < lastOption; option++) {
            int y = contentTop + (option - firstOption) * ROW_HEIGHT;
            String key = OPTION_KEYS[option];
            graphics.drawString(font, Component.translatable("unabled.config." + key), panelLeft + 8, y, 0xFFFFFF);
            Component description = Component.translatable("unabled.config.description." + key);
            List<FormattedCharSequence> lines = font.split(description, panelWidth - 16);
            for (int line = 0; line < Math.min(lines.size(), 2); line++) {
                graphics.drawString(font, lines.get(line), panelLeft + 8, y + 12 + line * 9, 0xA0A0A0);
            }
        }

        Component state = editable
                ? Component.translatable("unabled.config.status.editing_world")
                : ForgeConfig.isLoaded()
                    ? Component.translatable("unabled.config.status.remote_world")
                    : Component.translatable("unabled.config.status.world_closed");
        graphics.drawCenteredString(font, state, width / 2, height - 43, 0xFFCC55);
        renderables.forEach(renderable -> renderable.render(graphics, mouseX, mouseY, partialTick));
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    private static final class ModeSlider extends AbstractSliderButton {
        private final IntConsumer update;

        private ModeSlider(int x, int y, int width, int height, int initial, IntConsumer update) {
            super(x, y, width, height, Component.empty(), (initial - 1) / 2.0);
            this.update = update;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.translatable("unabled.config.mode_value", mode()));
        }

        @Override
        protected void applyValue() {
            update.accept(mode());
        }

        private int mode() {
            return (int) Math.round(value * 2.0) + 1;
        }
    }

    private static final class DoubleSlider extends AbstractSliderButton {
        private final double min;
        private final double max;
        private final double step;
        private final DoubleConsumer update;

        private DoubleSlider(int x, int y, int width, int height, double initial, double min,
                             double max, double step, DoubleConsumer update) {
            super(x, y, width, height, Component.empty(), (initial - min) / (max - min));
            this.min = min;
            this.max = max;
            this.step = step;
            this.update = update;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.translatable("unabled.config.slider_value", value()));
        }

        @Override
        protected void applyValue() {
            update.accept(value());
        }

        private double value() {
            double raw = min + value * (max - min);
            return Math.round(raw / step) * step;
        }
    }
}
