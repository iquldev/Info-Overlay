package iquldev.fpsoverlay.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.awt.Color;
import java.util.Arrays;


public class YACLConfigScreen {
    public static Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
            .title(Text.translatable("fpsoverlay.title"))
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("fpsoverlay.category.general"))
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("fpsoverlay.group.display"))
                    .description(OptionDescription.of(Text.translatable("fpsoverlay.group.display.desc")))
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.isShowed"))
                        .binding(true, () -> InfoOverlayConfig.isShowed, val -> InfoOverlayConfig.isShowed = val)
                        .controller(BooleanControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.isShowed = val)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.isAdvancedShowed"))
                        .binding(false, () -> InfoOverlayConfig.isAdvancedShowed, val -> InfoOverlayConfig.isAdvancedShowed = val)
                        .controller(BooleanControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.isAdvancedShowed = val)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.isClassicStyle"))
                        .binding(false, () -> InfoOverlayConfig.isClassicStyle, val -> InfoOverlayConfig.isClassicStyle = val)
                        .controller(BooleanControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.isClassicStyle = val)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.isVertical"))
                        .binding(false, () -> InfoOverlayConfig.isVertical, val -> InfoOverlayConfig.isVertical = val)
                        .controller(BooleanControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.isVertical = val)
                        .build())
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("fpsoverlay.group.position"))
                    .option(Option.<InfoOverlayConfig.OverlayPosition>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.overlayPosition"))
                        .binding(InfoOverlayConfig.OverlayPosition.TOP_LEFT, () -> InfoOverlayConfig.overlayPosition, val -> InfoOverlayConfig.overlayPosition = val)
                        .controller(opt -> CyclingListControllerBuilder.create(opt)
                            .values(Arrays.asList(InfoOverlayConfig.OverlayPosition.values()))
                            .formatValue(v -> Text.translatable("fpsoverlay.enum.OverlayPosition." + v.name())))
                        .listener((opt, val) -> InfoOverlayConfig.overlayPosition = val)
                        .build())
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("fpsoverlay.category.main_widget"))
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("fpsoverlay.group.template"))
                    .option(Option.<String>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.overlayText"))
                        .binding("{fps} FPS", () -> InfoOverlayConfig.overlayText, val -> InfoOverlayConfig.overlayText = val)
                        .controller(StringControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.overlayText = val)
                        .description(OptionDescription.of(Text.translatable("fpsoverlay.placeholders.description")))
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("fpsoverlay.group.appearance"))
                    .option(Option.<Color>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.overlayBackgroundColor"))
                        .binding(new Color(0, 0, 0, 127),
                                () -> hexToColor(InfoOverlayConfig.overlayBackgroundColor, InfoOverlayConfig.overlayTransparency),
                                val -> {
                                    InfoOverlayConfig.overlayBackgroundColor = colorToHex(val);
                                    InfoOverlayConfig.overlayTransparency = colorToTransparency(val);
                                })
                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                        .listener((opt, val) -> {
                            InfoOverlayConfig.overlayBackgroundColor = colorToHex(val);
                            InfoOverlayConfig.overlayTransparency = colorToTransparency(val);
                        })
                        .build())
                    .option(Option.<Color>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.overlayTextColor"))
                        .binding(Color.WHITE, () -> hexToColor(InfoOverlayConfig.overlayTextColor, 100), val -> InfoOverlayConfig.overlayTextColor = colorToHex(val) )
                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                        .listener((opt, val) -> InfoOverlayConfig.overlayTextColor = colorToHex(val))
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.overlayRounding"))
                        .binding(4, () -> InfoOverlayConfig.overlayRounding, val -> InfoOverlayConfig.overlayRounding = val)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 20).step(1))
                        .listener((opt, val) -> InfoOverlayConfig.overlayRounding = val)
                        .build())
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("fpsoverlay.group.dynamic"))
                    .option(Option.<String>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.overlayDynamicText"))
                        .description(OptionDescription.of(Text.translatable("fpsoverlay.option.overlayDynamicDesc")))
                        .binding("", () -> InfoOverlayConfig.overlayDynamicText, val -> InfoOverlayConfig.overlayDynamicText = val)
                        .controller(StringControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.overlayDynamicText = val)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.overlayDynamicInterval"))
                        .binding(3, () -> InfoOverlayConfig.overlayDynamicInterval, val -> InfoOverlayConfig.overlayDynamicInterval = val)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 60).step(1))
                        .listener((opt, val) -> InfoOverlayConfig.overlayDynamicInterval = val)
                        .build())

                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("fpsoverlay.category.advanced_widget"))
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("fpsoverlay.group.template"))
                    .option(Option.<String>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.advancedText"))
                        .binding("{minFps} / {maxFps}", () -> InfoOverlayConfig.advancedText, val -> InfoOverlayConfig.advancedText = val)
                        .controller(StringControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.advancedText = val)
                        .description(OptionDescription.of(Text.translatable("fpsoverlay.placeholders.description")))
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("fpsoverlay.group.appearance"))
                    .option(Option.<Color>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.advancedBackgroundColor"))
                        .binding(new Color(0, 0, 0, 127),
                                () -> hexToColor(InfoOverlayConfig.advancedBackgroundColor, InfoOverlayConfig.advancedTransparency),
                                val -> {
                                    InfoOverlayConfig.advancedBackgroundColor = colorToHex(val);
                                    InfoOverlayConfig.advancedTransparency = colorToTransparency(val);
                                })
                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                        .listener((opt, val) -> {
                            InfoOverlayConfig.advancedBackgroundColor = colorToHex(val);
                            InfoOverlayConfig.advancedTransparency = colorToTransparency(val);
                        })
                        .build())
                    .option(Option.<Color>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.advancedTextColor"))
                        .binding(Color.WHITE, () -> hexToColor(InfoOverlayConfig.advancedTextColor, 100), val -> InfoOverlayConfig.advancedTextColor = colorToHex(val))
                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                        .listener((opt, val) -> InfoOverlayConfig.advancedTextColor = colorToHex(val))
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.advancedRounding"))
                        .binding(4, () -> InfoOverlayConfig.advancedRounding, val -> InfoOverlayConfig.advancedRounding = val)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 20).step(1))
                        .listener((opt, val) -> InfoOverlayConfig.advancedRounding = val)
                        .build())
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("fpsoverlay.group.dynamic"))
                    .option(Option.<String>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.advancedDynamicText"))
                        .description(OptionDescription.of(Text.translatable("fpsoverlay.option.advancedDynamicDesc")))
                        .binding("", () -> InfoOverlayConfig.advancedDynamicText, val -> InfoOverlayConfig.advancedDynamicText = val)
                        .controller(StringControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.advancedDynamicText = val)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.advancedDynamicInterval"))
                        .binding(3, () -> InfoOverlayConfig.advancedDynamicInterval, val -> InfoOverlayConfig.advancedDynamicInterval = val)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 60).step(1))
                        .listener((opt, val) -> InfoOverlayConfig.advancedDynamicInterval = val)
                        .build())

                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("fpsoverlay.category.performance"))
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("fpsoverlay.group.thresholds"))
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.colorOnThreshold"))
                        .binding(false, () -> InfoOverlayConfig.colorOnThreshold, val -> InfoOverlayConfig.colorOnThreshold = val)
                        .controller(BooleanControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.colorOnThreshold = val)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("fpsoverlay.option.colorThreshold"))
                        .binding(30, () -> InfoOverlayConfig.colorThreshold, val -> InfoOverlayConfig.colorThreshold = val)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(5, 500).step(5))
                        .listener((opt, val) -> InfoOverlayConfig.colorThreshold = val)
                        .build())

                    .build())
                .build())
            .save(InfoOverlayConfig::save)
            .build()
            .generateScreen(parent);
    }

    private static Color hexToColor(String hex, int transparency) {
        if (hex == null || hex.isEmpty()) return Color.BLACK;
        try {
            Color c = Color.decode(hex.startsWith("#") ? hex : "#" + hex);
            int alpha = (int) (Math.min(100, Math.max(0, transparency)) * 2.55);
            return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
        } catch (Exception e) {}
        return Color.BLACK;
    }

    private static String colorToHex(Color color) {
        if (color == null) return "#000000";
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private static int colorToTransparency(Color color) {
        if (color == null) return 100;
        return (int) (color.getAlpha() / 2.55);
    }
}