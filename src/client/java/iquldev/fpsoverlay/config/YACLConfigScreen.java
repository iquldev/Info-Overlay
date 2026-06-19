package iquldev.fpsoverlay.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.awt.Color;
import java.util.Arrays;

public class YACLConfigScreen {
    public static Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("fpsoverlay.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("fpsoverlay.category.general"))
                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("fpsoverlay.group.display"))
                                .description(
                                        OptionDescription.of(Component.translatable("fpsoverlay.group.display.desc")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.isShowed"))
                                        .binding(true, () -> InfoOverlayConfig.isShowed,
                                                val -> InfoOverlayConfig.isShowed = val)
                                        .controller(BooleanControllerBuilder::create)
                                        .addListener((opt, event) -> InfoOverlayConfig.isShowed = opt.pendingValue())
                                        .description(OptionDescription.of(Component.translatable("fpsoverlay.option.isShowed.tooltip")))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.isAdvancedShowed"))
                                        .binding(false, () -> InfoOverlayConfig.isAdvancedShowed,
                                                val -> InfoOverlayConfig.isAdvancedShowed = val)
                                        .controller(BooleanControllerBuilder::create)
                                        .addListener(
                                                (opt, event) -> InfoOverlayConfig.isAdvancedShowed = opt.pendingValue())
                                        .description(OptionDescription.of(Component.translatable("fpsoverlay.option.isAdvancedShowed.tooltip")))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.isClassicStyle"))
                                        .binding(false, () -> InfoOverlayConfig.isClassicStyle,
                                                val -> InfoOverlayConfig.isClassicStyle = val)
                                        .controller(BooleanControllerBuilder::create)
                                        .addListener(
                                                (opt, event) -> InfoOverlayConfig.isClassicStyle = opt.pendingValue())
                                        .description(OptionDescription.of(Component.translatable("fpsoverlay.option.isClassicStyle.tooltip")))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.isVertical"))
                                        .binding(false, () -> InfoOverlayConfig.isVertical,
                                                val -> InfoOverlayConfig.isVertical = val)
                                        .controller(BooleanControllerBuilder::create)
                                        .addListener((opt, event) -> InfoOverlayConfig.isVertical = opt.pendingValue())
                                        .description(OptionDescription.of(Component.translatable("fpsoverlay.option.isVertical.tooltip")))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("fpsoverlay.group.position"))
                                .option(Option.<InfoOverlayConfig.OverlayPosition>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.overlayPosition"))
                                        .binding(InfoOverlayConfig.OverlayPosition.TOP_LEFT,
                                                () -> InfoOverlayConfig.overlayPosition,
                                                val -> InfoOverlayConfig.overlayPosition = val)
                                        .controller(opt -> CyclingListControllerBuilder.create(opt)
                                                .values(Arrays.asList(InfoOverlayConfig.OverlayPosition.values()))
                                                .formatValue(v -> Component
                                                        .translatable("fpsoverlay.enum.OverlayPosition." + v.name())))
                                        .addListener(
                                                (opt, event) -> InfoOverlayConfig.overlayPosition = opt.pendingValue())
                                        .description(OptionDescription.of(Component.translatable("fpsoverlay.option.overlayPosition.tooltip")))
                                        .build())
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("fpsoverlay.category.main_widget"))
                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("fpsoverlay.group.template"))
                                .option(Option.<String>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.overlayText"))
                                        .binding("{fps} FPS", () -> InfoOverlayConfig.overlayText,
                                                val -> InfoOverlayConfig.overlayText = val)
                                        .controller(StringControllerBuilder::create)
                                        .addListener((opt, event) -> InfoOverlayConfig.overlayText = opt.pendingValue())
                                        .description(OptionDescription
                                                .of(Component.translatable("fpsoverlay.placeholders.description")))
                                        .build())
                                .build())

                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("fpsoverlay.group.appearance"))
                                .option(Option.<Color>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.overlayBackgroundColor"))
                                        .binding(new Color(0, 0, 0, 127),
                                                () -> hexToColor(InfoOverlayConfig.overlayBackgroundColor,
                                                        InfoOverlayConfig.overlayTransparency),
                                                val -> {
                                                    InfoOverlayConfig.overlayBackgroundColor = colorToHex(val);
                                                    InfoOverlayConfig.overlayTransparency = colorToTransparency(val);
                                                })
                                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                        .addListener((opt, event) -> {
                                            Color val = opt.pendingValue();
                                            InfoOverlayConfig.overlayBackgroundColor = colorToHex(val);
                                            InfoOverlayConfig.overlayTransparency = colorToTransparency(val);
                                        })
                                        .description(OptionDescription.of(Component.translatable("fpsoverlay.option.overlayBackgroundColor.tooltip")))
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.overlayTextColor"))
                                        .binding(Color.WHITE, () -> hexToColor(InfoOverlayConfig.overlayTextColor, 100),
                                                val -> InfoOverlayConfig.overlayTextColor = colorToHex(val))
                                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                        .addListener((opt,
                                                event) -> InfoOverlayConfig.overlayTextColor = colorToHex(
                                                        opt.pendingValue()))
                                        .description(OptionDescription.of(Component.translatable("fpsoverlay.option.overlayTextColor.tooltip")))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.overlayRounding"))
                                        .binding(4, () -> InfoOverlayConfig.overlayRounding,
                                                val -> InfoOverlayConfig.overlayRounding = val)
                                        .controller(
                                                opt -> IntegerSliderControllerBuilder.create(opt).range(0, 20).step(1))
                                        .addListener(
                                                (opt, event) -> InfoOverlayConfig.overlayRounding = opt.pendingValue())
                                        .description(OptionDescription.of(Component.translatable("fpsoverlay.option.overlayRounding.tooltip")))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("fpsoverlay.group.dynamic"))
                                .option(Option.<String>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.overlayDynamicText"))
                                        .description(OptionDescription
                                                .of(Component.translatable("fpsoverlay.option.overlayDynamicText.tooltip")))
                                        .binding("", () -> InfoOverlayConfig.overlayDynamicText,
                                                val -> InfoOverlayConfig.overlayDynamicText = val)
                                        .controller(StringControllerBuilder::create)
                                        .addListener((opt,
                                                event) -> InfoOverlayConfig.overlayDynamicText = opt.pendingValue())
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.overlayDynamicInterval"))
                                        .binding(3, () -> InfoOverlayConfig.overlayDynamicInterval,
                                                val -> InfoOverlayConfig.overlayDynamicInterval = val)
                                        .controller(
                                                opt -> IntegerSliderControllerBuilder.create(opt).range(1, 60).step(1))
                                        .addListener((opt,
                                                event) -> InfoOverlayConfig.overlayDynamicInterval = opt.pendingValue())
                                        .description(OptionDescription.of(Component.translatable("fpsoverlay.option.overlayDynamicInterval.tooltip")))
                                        .build())

                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("fpsoverlay.category.advanced_widget"))
                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("fpsoverlay.group.template"))
                                .option(Option.<String>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.advancedText"))
                                        .binding("{minFps} / {maxFps}", () -> InfoOverlayConfig.advancedText,
                                                val -> InfoOverlayConfig.advancedText = val)
                                        .controller(StringControllerBuilder::create)
                                        .addListener(
                                                (opt, event) -> InfoOverlayConfig.advancedText = opt.pendingValue())
                                        .description(OptionDescription
                                                .of(Component.translatable("fpsoverlay.placeholders.description")))
                                        .build())
                                .build())

                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("fpsoverlay.group.appearance"))
                                .option(Option.<Color>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.advancedBackgroundColor"))
                                        .binding(new Color(0, 0, 0, 127),
                                                () -> hexToColor(InfoOverlayConfig.advancedBackgroundColor,
                                                        InfoOverlayConfig.advancedTransparency),
                                                val -> {
                                                    InfoOverlayConfig.advancedBackgroundColor = colorToHex(val);
                                                    InfoOverlayConfig.advancedTransparency = colorToTransparency(val);
                                                })
                                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                        .addListener((opt, event) -> {
                                            Color val = opt.pendingValue();
                                            InfoOverlayConfig.advancedBackgroundColor = colorToHex(val);
                                            InfoOverlayConfig.advancedTransparency = colorToTransparency(val);
                                        })
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.advancedTextColor"))
                                        .binding(Color.WHITE,
                                                () -> hexToColor(InfoOverlayConfig.advancedTextColor, 100),
                                                val -> InfoOverlayConfig.advancedTextColor = colorToHex(val))
                                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                        .addListener((opt,
                                                event) -> InfoOverlayConfig.advancedTextColor = colorToHex(
                                                        opt.pendingValue()))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.advancedRounding"))
                                        .binding(4, () -> InfoOverlayConfig.advancedRounding,
                                                val -> InfoOverlayConfig.advancedRounding = val)
                                        .controller(
                                                opt -> IntegerSliderControllerBuilder.create(opt).range(0, 20).step(1))
                                        .addListener(
                                                (opt, event) -> InfoOverlayConfig.advancedRounding = opt.pendingValue())
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("fpsoverlay.group.dynamic"))
                                .option(Option.<String>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.advancedDynamicText"))
                                        .description(OptionDescription
                                                .of(Component.translatable("fpsoverlay.option.overlayDynamicText.tooltip")))
                                        .binding("", () -> InfoOverlayConfig.advancedDynamicText,
                                                val -> InfoOverlayConfig.advancedDynamicText = val)
                                        .controller(StringControllerBuilder::create)
                                        .addListener((opt,
                                                event) -> InfoOverlayConfig.advancedDynamicText = opt.pendingValue())
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.advancedDynamicInterval"))
                                        .binding(3, () -> InfoOverlayConfig.advancedDynamicInterval,
                                                val -> InfoOverlayConfig.advancedDynamicInterval = val)
                                        .controller(
                                                opt -> IntegerSliderControllerBuilder.create(opt).range(1, 60).step(1))
                                        .addListener((opt,
                                                event) -> InfoOverlayConfig.advancedDynamicInterval = opt
                                                         .pendingValue())
                                        .description(OptionDescription.of(Component.translatable("fpsoverlay.option.overlayDynamicInterval.tooltip")))
                                        .build())
 
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("fpsoverlay.category.performance"))
                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("fpsoverlay.group.thresholds"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.colorOnThreshold"))
                                        .binding(false, () -> InfoOverlayConfig.colorOnThreshold,
                                                val -> InfoOverlayConfig.colorOnThreshold = val)
                                        .controller(BooleanControllerBuilder::create)
                                        .addListener(
                                                (opt, event) -> InfoOverlayConfig.colorOnThreshold = opt.pendingValue())
                                        .description(OptionDescription.of(Component.translatable("fpsoverlay.option.colorOnThreshold.tooltip")))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.translatable("fpsoverlay.option.colorThreshold"))
                                        .binding(30, () -> InfoOverlayConfig.colorThreshold,
                                                val -> InfoOverlayConfig.colorThreshold = val)
                                        .controller(
                                                opt -> IntegerSliderControllerBuilder.create(opt).range(5, 500).step(5))
                                        .addListener(
                                                (opt, event) -> InfoOverlayConfig.colorThreshold = opt.pendingValue())
                                        .description(OptionDescription.of(Component.translatable("fpsoverlay.option.colorThreshold.tooltip")))
                                        .build())

                                .build())
                        .build())
                .save(InfoOverlayConfig::save)
                .build()
                .generateScreen(parent);
    }

    private static Color hexToColor(String hex, int transparency) {
        if (hex == null || hex.isEmpty())
            return Color.BLACK;
        try {
            Color c = Color.decode(hex.startsWith("#") ? hex : "#" + hex);
            int alpha = (int) (Math.min(100, Math.max(0, transparency)) * 2.55);
            return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
        } catch (Exception e) {
        }
        return Color.BLACK;
    }

    private static String colorToHex(Color color) {
        if (color == null)
            return "#000000";
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private static int colorToTransparency(Color color) {
        if (color == null)
            return 100;
        return (int) (color.getAlpha() / 2.55);
    }
}