package iquldev.fpsoverlay.stats;

public class FpsStats {
    private int minFps = Integer.MAX_VALUE;
    private int maxFps = Integer.MIN_VALUE;
    private int currentFps = 0;
    private long lastUpdateTime = System.currentTimeMillis();
    private static final int UPDATE_INTERVAL = 5000;

    public void update(int fps) {
        this.currentFps = fps;
        
        if (fps < minFps) {
            minFps = fps;
        }
        if (fps > maxFps) {
            maxFps = fps;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime >= UPDATE_INTERVAL) {
            reset();
            lastUpdateTime = currentTime;
        }
    }

    public void reset() {
        minFps = currentFps;
        maxFps = currentFps;
    }

    public int getCurrent() {
        return currentFps;
    }

    public int getMin() {
        return minFps;
    }

    public int getMax() {
        return maxFps;
    }
}
