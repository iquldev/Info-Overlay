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

    private static long lastNonFpsUpdateTime = 0;
    private static final long NON_FPS_UPDATE_INTERVAL = 50;
    
    private static String cachedX = "0.0", cachedY = "0.0", cachedZ = "0.0";
    private static String cachedSystemTime = "", cachedWorldTime = "", cachedDayTime = "";
    private static String cachedWeather = "", cachedFacing = "", cachedSpeed = "";

    public static String format(String text, MinecraftClient client, OverlayStats stats) {
        if (client.player == null || client.world == null) return text;

        long now = System.currentTimeMillis();
        if (now - lastNonFpsUpdateTime >= NON_FPS_UPDATE_INTERVAL) {
            updateCachedStats(client, stats);
            lastNonFpsUpdateTime = now;
        }

        StringBuilder sb = new StringBuilder(text);
        replace(sb, "{fps}", maybeColor(stats.fps().getCurrent()));
        replace(sb, "{minFps}", maybeColor(stats.fps().getMin()));
        replace(sb, "{maxFps}", maybeColor(stats.fps().getMax()));
        
        replace(sb, "{x}", cachedX);
        replace(sb, "{y}", cachedY);
        replace(sb, "{z}", cachedZ);
        
        replace(sb, "{systemTime}", cachedSystemTime);
        replace(sb, "{worldTime}", cachedWorldTime);
        replace(sb, "{dayTime}", cachedDayTime);
        replace(sb, "{weather}", cachedWeather);
        
        replace(sb, "{currentRam}", stats.system().getCurrentRamMb());
        replace(sb, "{maxRam}", stats.system().getMaxRamMb());
        replace(sb, "{ramPercent}", stats.system().getRamPercent());
        
        replace(sb, "{sessionTime}", stats.session().getFormattedDuration());
        replace(sb, "{facing}", cachedFacing);
        replace(sb, "{playerSpeed}", cachedSpeed);

        return sb.toString();
    }

    private static void updateCachedStats(MinecraftClient client, OverlayStats stats) {
        cachedX = String.format("%.1f", client.player.getX());
        cachedY = String.format("%.1f", client.player.getY());
        cachedZ = String.format("%.1f", client.player.getZ());
        
        cachedSystemTime = getSystemTime();
        cachedWorldTime = getWorldTime(client);
        cachedDayTime = getDayTime(client);
        cachedWeather = getWeather(client);
        cachedFacing = getFacing(client);
        cachedSpeed = getSpeed(client);
    }

    private static void replace(StringBuilder sb, String target, String replacement) {
        int index = sb.indexOf(target);
        while (index != -1) {
            sb.replace(index, index + target.length(), replacement);
            index = sb.indexOf(target, index + replacement.length());
        }
    }

    private static String maybeColor(int value) {
        if (!InfoOverlayConfig.colorOnThreshold) return String.valueOf(value);
        return value < InfoOverlayConfig.colorThreshold ? "§c" + value + "§r" : String.valueOf(value);
    }

    private static String getSystemTime() {
        long now = System.currentTimeMillis();
        return String.format("%tH:%tM %s", now, now, getClockIcon(now));
    }

    private static String getClockIcon(long time) {
        int hour = Integer.parseInt(String.format("%tH", time));
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
