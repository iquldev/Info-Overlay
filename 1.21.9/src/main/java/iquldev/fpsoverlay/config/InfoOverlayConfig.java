package iquldev.fpsoverlay.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class InfoOverlayConfig extends MidnightConfig {
    @Comment(category = "a", centered = true) public static Comment display;
    @Entry(category = "a") public static boolean isShowed = true;
    @Entry(category = "a") public static boolean isAdvancedShowed = false;
    @Entry(category = "a") public static boolean isClassicStyle = false;
    @Entry(category = "a") public static boolean isVertical = false;
    @Comment(category = "a", centered = true) public static Comment position;
    @Entry(category = "a") public static OverlayPosition overlayPosition = OverlayPosition.TOP_LEFT;
    public enum OverlayPosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    @Comment(category = "a", centered = true) public static Comment colorOnThresholdComment;
    @Entry(category = "a") public static boolean colorOnThreshold = false;
    @Entry(category = "a", isSlider = true, min = 5, max = 1000) public static int colorThreshold = 30;

    @Comment(category = "b", centered = true) public static Comment mainSettings;
    @Entry(category = "b") public static String overlayText = "{fps} FPS";
    @Comment(category = "b", centered = true) public static Comment overlayColor;
    @Entry(category = "b", width = 7, min = 7, isColor = true) public static String overlayBackgroundColor = "#000000";
    @Entry(category = "b", width = 7, min = 7, isColor = true) public static String overlayTextColor = "#ffffff";
    @Entry(category = "b", isSlider = true, min = 0, max = 100, precision = 1) public static int overlayTransparency = 50;
    @Entry(category = "b", isSlider = true, min = 0, max = 20) public static int overlayRounding = 4;
    @Comment(category = "b", centered = true) public static Comment overlayDynamicTitle;
    @Comment(category = "b", centered = true) public static Comment overlayDynamicDesc;
    @Entry(category = "b") public static String overlayDynamicText = "";
    @Entry(category = "b") public static int overlayDynamicInterval = 3;

    @Comment(category = "c", centered = true) public static Comment advancedSettings;
    @Entry(category = "c") public static String advancedText = "{minFps} / {maxFps}";
    @Comment(category = "c", centered = true) public static Comment advancedColor;
    @Entry(category = "c", width = 7, min = 7, isColor = true) public static String advancedBackgroundColor = "#000000";
    @Entry(category = "c", width = 7, min = 7, isColor = true) public static String advancedTextColor = "#ffffff";
    @Entry(category = "c", isSlider = true, min = 0, max = 100, precision = 1) public static int advancedTransparency = 50;
    @Entry(category = "c", isSlider = true, min = 0, max = 20) public static int advancedRounding = 4;
    @Comment(category = "c", centered = true) public static Comment advancedDynamicTitle;
    @Comment(category = "c", centered = true) public static Comment advancedDynamicDesc;
    @Entry(category = "c") public static String advancedDynamicText = "";
    @Entry(category = "c") public static int advancedDynamicInterval = 3;

    @Comment(category = "d", centered = true) public static Comment placeholders;
    @Comment(category = "d") public static Comment fpsPlaceholder;
    @Comment(category = "d") public static Comment coordsPlaceholder;
    @Comment(category = "d") public static Comment systemTimePlaceholder;
    @Comment(category = "d") public static Comment dayTimePlaceholder;
    @Comment(category = "d") public static Comment currentRamPlaceholder;
    @Comment(category = "d") public static Comment maxRamPlaceholder;
    @Comment(category = "d") public static Comment ramPercentPlaceholder;
    @Comment(category = "d") public static Comment worldTimePlaceholder;
    @Comment(category = "d") public static Comment weatherPlaceholder;
    @Comment(category = "d") public static Comment sessionTimePlaceholder;
    @Comment(category = "d") public static Comment minMaxFpsPlaceholder;
    @Comment(category = "d") public static Comment facingPlaceholder;
    @Comment(category = "d") public static Comment speedPlaceholder;
}