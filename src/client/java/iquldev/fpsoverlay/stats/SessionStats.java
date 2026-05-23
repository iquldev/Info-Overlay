package iquldev.fpsoverlay.stats;

public class SessionStats {
    private final long startTime = System.currentTimeMillis();

    public String getFormattedDuration() {
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        return String.format("%02d:%02d:%02d", elapsed / 3600, (elapsed % 3600) / 60, elapsed % 60);
    }
}
