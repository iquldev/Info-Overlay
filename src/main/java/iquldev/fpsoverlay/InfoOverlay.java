package iquldev.fpsoverlay;

import iquldev.fpsoverlay.config.InfoOverlayConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfoOverlay implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Info Overlay");

    @Override
    public void onInitialize() {
        InfoOverlayConfig.load();
        LOGGER.info("Info Overlay loaded!");
    }
}
