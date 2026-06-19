package iquldev.fpsoverlay.config;

import iquldev.fpsoverlay.config.InfoOverlayConfig.OverlayPosition;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class SodiumConfigEntryPoint implements ConfigEntryPoint {
        private static final Identifier ID_SHOW_OVERLAY = id("show_overlay");
        private static final Identifier ID_SHOW_ADVANCED = id("show_advanced");
        private static final Identifier ID_CLASSIC_STYLE = id("classic_style");
        private static final Identifier ID_VERTICAL = id("vertical_layout");
        private static final Identifier ID_POSITION = id("overlay_position");
        private static final Identifier ID_COLOR_ON_THRESHOLD = id("color_on_threshold");
        private static final Identifier ID_FULL_SETTINGS = id("full_settings");

        @SuppressWarnings("null")
        private static Identifier id(String path) {
                return Identifier.fromNamespaceAndPath("fpsoverlay", path);
        }

        @Override
        @SuppressWarnings("null")
        public void registerConfigLate(ConfigBuilder builder) {
                builder.registerOwnModOptions()
                                .setIcon(Identifier.parse("fpsoverlay:textures/icon.png"))
                                .addPage(builder.createOptionPage()
                                                .setName(Component.translatable("fpsoverlay.title"))
                                                .addOptionGroup(builder.createOptionGroup()
                                                                .setName(Component.translatable(
                                                                                "fpsoverlay.group.display"))
                                                                .addOption(builder.createBooleanOption(ID_SHOW_OVERLAY)
                                                                                .setName(Component.translatable(
                                                                                                "fpsoverlay.option.isShowed"))
                                                                                .setTooltip(Component.translatable(
                                                                                                "fpsoverlay.option.isShowed.tooltip"))
                                                                                .setStorageHandler(
                                                                                                InfoOverlayConfig::save)
                                                                                .setBinding(val -> InfoOverlayConfig.isShowed = val,
                                                                                                () -> InfoOverlayConfig.isShowed)
                                                                                .setDefaultValue(true))
                                                                .addOption(builder.createBooleanOption(ID_SHOW_ADVANCED)
                                                                                .setName(Component.translatable(
                                                                                                "fpsoverlay.option.isAdvancedShowed"))
                                                                                .setTooltip(
                                                                                                Component.translatable(
                                                                                                                "fpsoverlay.option.isAdvancedShowed.tooltip"))
                                                                                .setStorageHandler(
                                                                                                InfoOverlayConfig::save)
                                                                                .setBinding(val -> InfoOverlayConfig.isAdvancedShowed = val,
                                                                                                () -> InfoOverlayConfig.isAdvancedShowed)
                                                                                .setDefaultValue(false))
                                                                .addOption(builder.createBooleanOption(ID_CLASSIC_STYLE)
                                                                                .setName(Component.translatable(
                                                                                                "fpsoverlay.option.isClassicStyle"))
                                                                                .setTooltip(Component.translatable(
                                                                                                "fpsoverlay.option.isClassicStyle.tooltip"))
                                                                                .setStorageHandler(
                                                                                                InfoOverlayConfig::save)
                                                                                .setBinding(val -> InfoOverlayConfig.isClassicStyle = val,
                                                                                                () -> InfoOverlayConfig.isClassicStyle)
                                                                                .setDefaultValue(false))
                                                                .addOption(builder.createBooleanOption(ID_VERTICAL)
                                                                                .setName(Component.translatable(
                                                                                                "fpsoverlay.option.isVertical"))
                                                                                .setTooltip(Component.translatable(
                                                                                                "fpsoverlay.option.isVertical.tooltip"))
                                                                                .setStorageHandler(
                                                                                                InfoOverlayConfig::save)
                                                                                .setBinding(val -> InfoOverlayConfig.isVertical = val,
                                                                                                () -> InfoOverlayConfig.isVertical)
                                                                                .setDefaultValue(false))
                                                                .addOption(builder
                                                                                .createEnumOption(ID_POSITION,
                                                                                                OverlayPosition.class)
                                                                                .setName(Component.translatable(
                                                                                                "fpsoverlay.option.overlayPosition"))
                                                                                .setTooltip(Component.translatable(
                                                                                                "fpsoverlay.option.overlayPosition.tooltip"))
                                                                                .setStorageHandler(
                                                                                                InfoOverlayConfig::save)
                                                                                .setBinding(val -> InfoOverlayConfig.overlayPosition = val,
                                                                                                () -> InfoOverlayConfig.overlayPosition)
                                                                                .setDefaultValue(
                                                                                                OverlayPosition.TOP_LEFT)
                                                                                .setElementNameProvider(
                                                                                                position -> Component
                                                                                                                .translatable(
                                                                                                                                "fpsoverlay.enum.OverlayPosition."
                                                                                                                                                + position.name()))))
                                                .addOptionGroup(builder.createOptionGroup()
                                                                .setName(Component.translatable(
                                                                                "fpsoverlay.group.thresholds"))
                                                                .addOption(builder
                                                                                .createBooleanOption(
                                                                                                ID_COLOR_ON_THRESHOLD)
                                                                                .setName(Component.translatable(
                                                                                                "fpsoverlay.option.colorOnThreshold"))
                                                                                .setTooltip(
                                                                                                Component.translatable(
                                                                                                                "fpsoverlay.option.colorOnThreshold.tooltip"))
                                                                                .setStorageHandler(
                                                                                                InfoOverlayConfig::save)
                                                                                .setBinding(val -> InfoOverlayConfig.colorOnThreshold = val,
                                                                                                () -> InfoOverlayConfig.colorOnThreshold)
                                                                                .setDefaultValue(false)))
                                                .addOptionGroup(builder.createOptionGroup()
                                                                .setName(Component
                                                                                .translatable("fpsoverlay.group.more"))
                                                                .addOption(builder
                                                                                .createExternalButtonOption(
                                                                                                ID_FULL_SETTINGS)
                                                                                .setName(Component.translatable(
                                                                                                "fpsoverlay.option.fullSettings"))
                                                                                .setTooltip(Component.translatable(
                                                                                                "fpsoverlay.option.fullSettings.tooltip"))
                                                                                .setScreenConsumer(parentScreen -> {
                                                                                        Screen yaclScreen = YACLConfigScreen
                                                                                                        .createScreen(parentScreen);
                                                                                        Minecraft.getInstance().gui
                                                                                                        .setScreen(yaclScreen);
                                                                                }))));
        }
}