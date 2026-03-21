package iquldev.fpsoverlay.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.awt.Color;

public class YACLConfigScreen {
    public static Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
            .title(Text.translatable("fpsoverlay.title"))
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("fpsoverlay.midnightconfig.display"))
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("fpsoverlay.midnightconfig.display"))
                    .description(OptionDescription.of(Text.translatable("fpsoverlay.preview.description")))
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("fpsoverlay.midnightconfig.isShowed"))
                        .binding(true, () -> InfoOverlayConfig.isShowed, val -> InfoOverlayConfig.isShowed = val)
                        .controller(BooleanControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.isShowed = val)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("fpsoverlay.midnightconfig.isAdvancedShowed"))
                        .binding(false, () -> InfoOverlayConfig.isAdvancedShowed, val -> InfoOverlayConfig.isAdvancedShowed = val)
                        .controller(BooleanControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.isAdvancedShowed = val)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("fpsoverlay.midnightconfig.isClassicStyle"))
                        .binding(false, () -> InfoOverlayConfig.isClassicStyle, val -> InfoOverlayConfig.isClassicStyle = val)
                        .controller(BooleanControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.isClassicStyle = val)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("fpsoverlay.midnightconfig.isVertical"))
                        .binding(false, () -> InfoOverlayConfig.isVertical, val -> InfoOverlayConfig.isVertical = val)
                        .controller(BooleanControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.isVertical = val)
                        .build())
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("fpsoverlay.midnightconfig.position"))
                    .option(Option.<InfoOverlayConfig.OverlayPosition>createBuilder()
                        .name(Text.translatable("fpsoverlay.midnightconfig.overlayPosition"))
                        .binding(InfoOverlayConfig.OverlayPosition.TOP_LEFT, () -> InfoOverlayConfig.overlayPosition, val -> InfoOverlayConfig.overlayPosition = val)
                        .controller(opt -> EnumControllerBuilder.create(opt)
                            .enumClass(InfoOverlayConfig.OverlayPosition.class)
                            .formatValue(v -> Text.translatable("fpsoverlay.midnightconfig.enum.OverlayPosition." + v.name())))
                        .listener((opt, val) -> InfoOverlayConfig.overlayPosition = val)
                        .build())
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("fpsoverlay.midnightconfig.colorOnThresholdComment"))
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("fpsoverlay.midnightconfig.colorOnThreshold"))
                        .binding(false, () -> InfoOverlayConfig.colorOnThreshold, val -> InfoOverlayConfig.colorOnThreshold = val)
                        .controller(BooleanControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.colorOnThreshold = val)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("fpsoverlay.midnightconfig.colorThreshold"))
                        .binding(30, () -> InfoOverlayConfig.colorThreshold, val -> InfoOverlayConfig.colorThreshold = val)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(5, 1000).step(5))
                        .listener((opt, val) -> InfoOverlayConfig.colorThreshold = val)
                        .build())
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("fpsoverlay.midnightconfig.category.b"))
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("fpsoverlay.midnightconfig.mainSettings"))
                    .option(Option.<String>createBuilder()
                        .name(Text.translatable("fpsoverlay.midnightconfig.overlayText"))
                        .description(OptionDescription.of(Text.translatable("fpsoverlay.placeholders.description")))
                        .binding("{fps} FPS", () -> InfoOverlayConfig.overlayText, val -> InfoOverlayConfig.overlayText = val)
                        .controller(StringControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.overlayText = val)
                        .build())
                    .option(Option.<Color>createBuilder()
                        .name(Text.translatable("fpsoverlay.midnightconfig.overlayBackgroundColor"))
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
                        .name(Text.translatable("fpsoverlay.midnightconfig.overlayTextColor"))
                        .binding(Color.WHITE, () -> hexToColor(InfoOverlayConfig.overlayTextColor, 100), val -> InfoOverlayConfig.overlayTextColor = colorToHex(val))
                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                        .listener((opt, val) -> InfoOverlayConfig.overlayTextColor = colorToHex(val))
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("fpsoverlay.midnightconfig.overlayRounding"))
                        .binding(4, () -> InfoOverlayConfig.overlayRounding, val -> InfoOverlayConfig.overlayRounding = val)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 20).step(1))
                        .listener((opt, val) -> InfoOverlayConfig.overlayRounding = val)
                        .build())
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("fpsoverlay.midnightconfig.overlayDynamicTitle"))
                    .option(Option.<String>createBuilder()
                        .name(Text.translatable("fpsoverlay.midnightconfig.overlayDynamicText"))
                        .description(OptionDescription.of(Text.translatable("fpsoverlay.placeholders.description"), Text.translatable("fpsoverlay.midnightconfig.overlayDynamicDesc")))
                        .binding("", () -> InfoOverlayConfig.overlayDynamicText, val -> InfoOverlayConfig.overlayDynamicText = val)
                        .controller(StringControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.overlayDynamicText = val)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("fpsoverlay.midnightconfig.overlayDynamicInterval"))
                        .binding(3, () -> InfoOverlayConfig.overlayDynamicInterval, val -> InfoOverlayConfig.overlayDynamicInterval = val)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 60).step(1))
                        .listener((opt, val) -> InfoOverlayConfig.overlayDynamicInterval = val)
                        .build())
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("fpsoverlay.midnightconfig.category.c"))
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("fpsoverlay.midnightconfig.advancedSettings"))
                    .option(Option.<String>createBuilder()
                        .name(Text.translatable("fpsoverlay.midnightconfig.advancedText"))
                        .description(OptionDescription.of(Text.translatable("fpsoverlay.placeholders.description")))
                        .binding("{minFps} / {maxFps}", () -> InfoOverlayConfig.advancedText, val -> InfoOverlayConfig.advancedText = val)
                        .controller(StringControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.advancedText = val)
                        .build())
                    .option(Option.<Color>createBuilder()
                        .name(Text.translatable("fpsoverlay.midnightconfig.advancedBackgroundColor"))
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
                        .name(Text.translatable("fpsoverlay.midnightconfig.advancedTextColor"))
                        .binding(Color.WHITE, () -> hexToColor(InfoOverlayConfig.advancedTextColor, 100), val -> InfoOverlayConfig.advancedTextColor = colorToHex(val))
                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                        .listener((opt, val) -> InfoOverlayConfig.advancedTextColor = colorToHex(val))
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("fpsoverlay.midnightconfig.advancedRounding"))
                        .binding(4, () -> InfoOverlayConfig.advancedRounding, val -> InfoOverlayConfig.advancedRounding = val)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 20).step(1))
                        .listener((opt, val) -> InfoOverlayConfig.advancedRounding = val)
                        .build())
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("fpsoverlay.midnightconfig.advancedDynamicTitle"))
                    .option(Option.<String>createBuilder()
                        .name(Text.translatable("fpsoverlay.midnightconfig.advancedDynamicText"))
                        .description(OptionDescription.of(Text.translatable("fpsoverlay.placeholders.description"), Text.translatable("fpsoverlay.midnightconfig.advancedDynamicDesc")))
                        .binding("", () -> InfoOverlayConfig.advancedDynamicText, val -> InfoOverlayConfig.advancedDynamicText = val)
                        .controller(StringControllerBuilder::create)
                        .listener((opt, val) -> InfoOverlayConfig.advancedDynamicText = val)
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("fpsoverlay.midnightconfig.advancedDynamicInterval"))
                        .binding(3, () -> InfoOverlayConfig.advancedDynamicInterval, val -> InfoOverlayConfig.advancedDynamicInterval = val)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 60).step(1))
                        .listener((opt, val) -> InfoOverlayConfig.advancedDynamicInterval = val)
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
