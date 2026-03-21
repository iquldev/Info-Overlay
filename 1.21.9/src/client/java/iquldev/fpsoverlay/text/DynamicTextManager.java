package iquldev.fpsoverlay.text;

import iquldev.fpsoverlay.config.InfoOverlayConfig;
import iquldev.fpsoverlay.stats.OverlayStats;
import net.minecraft.client.MinecraftClient;

public class DynamicTextManager {
    private boolean useOverlayDynamicText = false;
    private boolean useAdvancedDynamicText = false;
    private long lastOverlaySwitchTime = System.currentTimeMillis();
    private long lastAdvancedSwitchTime = System.currentTimeMillis();

    public void updateDynamicText() {
        long currentTime = System.currentTimeMillis();

        if (InfoOverlayConfig.overlayDynamicInterval > 0 && 
            !InfoOverlayConfig.overlayDynamicText.isEmpty() && 
            currentTime - lastOverlaySwitchTime >= InfoOverlayConfig.overlayDynamicInterval * 1000L) {
            useOverlayDynamicText = !useOverlayDynamicText;
            lastOverlaySwitchTime = currentTime;
        }

        if (InfoOverlayConfig.advancedDynamicInterval > 0 && 
            !InfoOverlayConfig.advancedDynamicText.isEmpty() &&  
            currentTime - lastAdvancedSwitchTime >= InfoOverlayConfig.advancedDynamicInterval * 1000L) {
            useAdvancedDynamicText = !useAdvancedDynamicText;
            lastAdvancedSwitchTime = currentTime;
        }
    }

    public String getOverlayText(MinecraftClient client, OverlayStats stats) {
        String textToFormat = (InfoOverlayConfig.overlayDynamicInterval > 0 && useOverlayDynamicText) ?
                              InfoOverlayConfig.overlayDynamicText :
                              InfoOverlayConfig.overlayText;
        
        return TextFormatter.format(textToFormat, client, stats);
    }

    public String getAdvancedText(MinecraftClient client, OverlayStats stats) {
        String textToFormat = (InfoOverlayConfig.advancedDynamicInterval > 0 && useAdvancedDynamicText) ?
                              InfoOverlayConfig.advancedDynamicText :
                              InfoOverlayConfig.advancedText;
        
        return TextFormatter.format(textToFormat, client, stats);
    }

    public boolean isUsingOverlayDynamicText() {
        return useOverlayDynamicText;
    }

    public boolean isUsingAdvancedDynamicText() {
        return useAdvancedDynamicText;
    }
}
