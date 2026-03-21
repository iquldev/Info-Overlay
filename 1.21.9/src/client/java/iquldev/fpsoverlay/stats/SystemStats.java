package iquldev.fpsoverlay.stats;

public class SystemStats {
    private static final long UPDATE_INTERVAL_MS = 2_000L;
    private static final long BYTES_PER_MB = 1024L * 1024L;
    private long lastUpdateTime = 0L;
    private String currentRamMb = "0";
    private String ramPercent = "0.0";

    public void update() {
        long now = System.currentTimeMillis();
        if (now - lastUpdateTime <= UPDATE_INTERVAL_MS) return;
        lastUpdateTime = now;

        Runtime rt = Runtime.getRuntime();
        long used = rt.totalMemory() - rt.freeMemory();
        long max = rt.maxMemory();
        
        currentRamMb = String.valueOf(used / BYTES_PER_MB);
        ramPercent = max > 0 ? String.format("%.1f", (double) used / max * 100.0) : "0.0";
    }

    public String getCurrentRamMb() { return currentRamMb; }
    public String getMaxRamMb() { return String.valueOf(Runtime.getRuntime().maxMemory() / BYTES_PER_MB); }
    public String getRamPercent() { return ramPercent; }
}
