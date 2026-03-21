package iquldev.fpsoverlay.text;

import iquldev.fpsoverlay.config.InfoOverlayConfig;
import iquldev.fpsoverlay.stats.OverlayStats;
import net.minecraft.client.MinecraftClient;

public class TextFormatter {
    private static final String ICON_SUN = "🌞";
    private static final String ICON_MOON = "🌙";
    private static final String ICON_RAIN = "🌧️";
    private static final String ICON_THUNDER = "⛈️";
    private static final String ICON_COMPASS = "🧭";
    private static final String ICON_SPEED = "⚡";
    private static final String ICON_CLOCK_MORNING = "🕖";
    private static final String ICON_CLOCK_DAY = "🕓";
    private static final String ICON_CLOCK_EVENING = "🕒";
    private static final String ICON_CLOCK_NIGHT = "🕰️";

    public static String format(String text, MinecraftClient client, OverlayStats stats) {
        if (client.player == null || client.world == null) return text;

        text = text.replace("{fps}", maybeColor(stats.fps().getCurrent()));
        text = text.replace("{minFps}", maybeColor(stats.fps().getMin()));
        text = text.replace("{maxFps}", maybeColor(stats.fps().getMax()));
        
        text = text.replace("{x}", String.format("%.1f", client.player.getX()));
        text = text.replace("{y}", String.format("%.1f", client.player.getY()));
        text = text.replace("{z}", String.format("%.1f", client.player.getZ()));
        
        text = text.replace("{systemTime}", getSystemTime());
        text = text.replace("{worldTime}", getWorldTime(client));
        text = text.replace("{dayTime}", getDayTime(client));
        text = text.replace("{weather}", getWeather(client));
        
        text = text.replace("{currentRam}", stats.system().getCurrentRamMb());
        text = text.replace("{maxRam}", stats.system().getMaxRamMb());
        text = text.replace("{ramPercent}", stats.system().getRamPercent());
        
        text = text.replace("{sessionTime}", stats.session().getFormattedDuration());
        text = text.replace("{facing}", getFacing(client));
        text = text.replace("{playerSpeed}", getSpeed(client));

        return text;
    }

    private static String maybeColor(int value) {
        if (!InfoOverlayConfig.colorOnThreshold) return String.valueOf(value);
        return value < InfoOverlayConfig.colorThreshold ? "§c" + value + "§r" : String.valueOf(value);
    }

    private static String getSystemTime() {
        return String.format("%tH:%tM %s", System.currentTimeMillis(), System.currentTimeMillis(), getClockIcon());
    }

    private static String getClockIcon() {
        int hour = Integer.parseInt(String.format("%tH", System.currentTimeMillis()));
        if (hour >= 6 && hour < 12) return ICON_CLOCK_MORNING;
        if (hour >= 12 && hour < 18) return ICON_CLOCK_DAY;
        if (hour >= 18 && hour < 22) return ICON_CLOCK_EVENING;
        return ICON_CLOCK_NIGHT;
    }

    private static String getWorldTime(MinecraftClient client) {
        long time = client.world.getTimeOfDay() % 24000L;
        int hours = (int) ((6 + (time / 1000) % 24) % 24);
        int minutes = (int) ((time % 1000) * 60 / 1000);
        return String.format("%02d:%02d %s", hours, minutes, getClockIconForHour(hours));
    }

    private static String getClockIconForHour(int hour) {
        if (hour >= 6 && hour < 12) return ICON_CLOCK_MORNING;
        if (hour >= 12 && hour < 18) return ICON_CLOCK_DAY;
        if (hour >= 18 && hour < 22) return ICON_CLOCK_EVENING;
        return ICON_CLOCK_NIGHT;
    }

    private static String getDayTime(MinecraftClient client) {
        return (client.world.getTimeOfDay() % 24000L < 13000L) ? "Day " + ICON_SUN : "Night " + ICON_MOON;
    }

    private static String getWeather(MinecraftClient client) {
        if (client.world.isThundering()) return "Thunder " + ICON_THUNDER;
        if (client.world.isRaining()) return "Rain " + ICON_RAIN;
        return "Clear " + ICON_SUN;
    }

    private static String getFacing(MinecraftClient client) {
        float yaw = (client.player.getYaw() % 360 + 360) % 360;
        String dir = "Unknown";
        if (yaw >= 315 || yaw < 45) dir = "South";
        else if (yaw >= 45 && yaw < 135) dir = "West";
        else if (yaw >= 135 && yaw < 225) dir = "North";
        else if (yaw >= 225 && yaw < 315) dir = "East";
        return dir + " " + ICON_COMPASS;
    }

    private static String getSpeed(MinecraftClient client) {
        double speed = Math.sqrt(client.player.getVelocity().horizontalLengthSquared() + Math.pow(client.player.getVelocity().y, 2));
        return String.format("%.2f %s", speed, ICON_SPEED);
    }
}
