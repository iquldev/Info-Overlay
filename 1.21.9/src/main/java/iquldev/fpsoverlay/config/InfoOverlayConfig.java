package iquldev.fpsoverlay.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import iquldev.fpsoverlay.InfoOverlay;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class InfoOverlayConfig {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "fpsoverlay.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static boolean isShowed = true;
    public static boolean isAdvancedShowed = false;
    public static boolean isClassicStyle = false;
    public static boolean isVertical = false;
    public static OverlayPosition overlayPosition = OverlayPosition.TOP_LEFT;

    public enum OverlayPosition {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    public static boolean colorOnThreshold = false;

    public static int colorThreshold = 30;

    public static String overlayText = "{fps} FPS";
    public static String overlayBackgroundColor = "#000000";
    public static String overlayTextColor = "#ffffff";
    public static int overlayTransparency = 50;
    public static int overlayRounding = 4;
    public static String overlayDynamicText = "";
    public static int overlayDynamicInterval = 3;

    public static String advancedText = "{minFps} / {maxFps}";
    public static String advancedBackgroundColor = "#000000";
    public static String advancedTextColor = "#ffffff";
    public static int advancedTransparency = 50;
    public static int advancedRounding = 4;
    public static String advancedDynamicText = "";
    public static int advancedDynamicInterval = 3;

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data != null) {
                isShowed = data.isShowed;
                isAdvancedShowed = data.isAdvancedShowed;
                isClassicStyle = data.isClassicStyle;
                isVertical = data.isVertical;
                overlayPosition = data.overlayPosition;
                colorOnThreshold = data.colorOnThreshold;

                colorThreshold = data.colorThreshold;
                overlayText = data.overlayText;
                overlayBackgroundColor = data.overlayBackgroundColor;
                overlayTextColor = data.overlayTextColor;
                overlayTransparency = data.overlayTransparency;
                overlayRounding = data.overlayRounding;
                overlayDynamicText = data.overlayDynamicText;
                overlayDynamicInterval = data.overlayDynamicInterval;
                advancedText = data.advancedText;
                advancedBackgroundColor = data.advancedBackgroundColor;
                advancedTextColor = data.advancedTextColor;
                advancedTransparency = data.advancedTransparency;
                advancedRounding = data.advancedRounding;
                advancedDynamicText = data.advancedDynamicText;
                advancedDynamicInterval = data.advancedDynamicInterval;
            }
        } catch (IOException e) {
            InfoOverlay.LOGGER.error("Failed to load config!", e);
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            ConfigData data = new ConfigData();
            data.isShowed = isShowed;
            data.isAdvancedShowed = isAdvancedShowed;
            data.isClassicStyle = isClassicStyle;
            data.isVertical = isVertical;
            data.overlayPosition = overlayPosition;
            data.colorOnThreshold = colorOnThreshold;

            data.colorThreshold = colorThreshold;
            data.overlayText = overlayText;
            data.overlayBackgroundColor = overlayBackgroundColor;
            data.overlayTextColor = overlayTextColor;
            data.overlayTransparency = overlayTransparency;
            data.overlayRounding = overlayRounding;
            data.overlayDynamicText = overlayDynamicText;
            data.overlayDynamicInterval = overlayDynamicInterval;
            data.advancedText = advancedText;
            data.advancedBackgroundColor = advancedBackgroundColor;
            data.advancedTextColor = advancedTextColor;
            data.advancedTransparency = advancedTransparency;
            data.advancedRounding = advancedRounding;
            data.advancedDynamicText = advancedDynamicText;
            data.advancedDynamicInterval = advancedDynamicInterval;
            GSON.toJson(data, writer);
        } catch (IOException e) {
            InfoOverlay.LOGGER.error("Failed to save config!", e);
        }
    }

    private static class ConfigData {
        boolean isShowed = true;
        boolean isAdvancedShowed = false;
        boolean isClassicStyle = false;
        boolean isVertical = false;
        OverlayPosition overlayPosition = OverlayPosition.TOP_LEFT;
        boolean colorOnThreshold = false;

        int colorThreshold = 30;
        String overlayText = "{fps} FPS";
        String overlayBackgroundColor = "#000000";
        String overlayTextColor = "#ffffff";
        int overlayTransparency = 50;
        int overlayRounding = 4;
        String overlayDynamicText = "";
        int overlayDynamicInterval = 3;
        String advancedText = "{minFps} / {maxFps}";
        String advancedBackgroundColor = "#000000";
        String advancedTextColor = "#ffffff";
        int advancedTransparency = 50;
        int advancedRounding = 4;
        String advancedDynamicText = "";
        int advancedDynamicInterval = 3;
    }
}